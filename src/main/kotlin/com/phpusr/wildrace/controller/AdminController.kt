package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.Post
import com.phpusr.wildrace.domain.PostRepo
import com.phpusr.wildrace.service.VKApiService
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional(readOnly = true)
@RequestMapping("admin")
@RestController
class AdminController(
        private val vkApiService: VKApiService,
        private val postRepo: PostRepo
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val vkPostsIds = mutableListOf<Int>()

    // Remove deleted posts in VK from DB
    @Transactional
    @GetMapping("removeAllDeletedPosts")
    fun removeAllDeletedPosts(): Map<String, Any> {
        logger.info("-------- Start Remove useless data from DB --------")


        var countPosts = vkApiService.wallGet(0, 1).count
        var downloadedCount = vkPostsIds.size

        if (downloadedCount > 0) {
            logger.info(">> From cache: ($downloadedCount/$countPosts)")
        }

        logger.info(">> Download posts from VK")

        while (downloadedCount < countPosts) {
            countPosts = downloadBlock(vkPostsIds, countPosts, downloadedCount, 100)
            downloadedCount = vkPostsIds.size

            logger.info("  -- Download after: ($downloadedCount/$countPosts)")

            if (downloadedCount > countPosts) {
                throw RuntimeException("Number of posts in DB > downloaded")
            }

            Thread.sleep(300)
        }

        val posts = removePosts(vkPostsIds)

        logger.info("-------- End Remove useless data from DB --------")

        return mapOf(
                "downloadedCount" to downloadedCount,
                "countPosts" to countPosts,
                "ids" to posts.joinToString(", ") { it.id.toString() },
                "posts" to posts.map(::postToMap)
        )
    }

    private fun downloadBlock(vkPostIds: MutableList<Int>, countPosts: Int, downloadedCount: Int, defDownloadCount: Int): Int {
        val leftCount = countPosts - downloadedCount
        val offset = if (leftCount > defDownloadCount) leftCount - defDownloadCount else 0
        val downloadCount = if (leftCount > defDownloadCount) defDownloadCount else leftCount
        val response = vkApiService.wallGet(offset, downloadCount)

        logger.info(" > Download before: ($downloadedCount/$countPosts) offset: ${offset}")

        if (response.count != countPosts) {
            logger.warn(" -- Number of posts in VK changed: $countPosts -> ${response.count}")
            return countPosts
        }

        vkPostIds.addAll(response.items.map { it.id })

        return countPosts
    }

    private fun removePosts(vkPostIds: MutableList<Int>): List<Post> {
        logger.info(">> Remove posts")
        val dbPosts = postRepo.findAll()
        val deletedPosts = dbPosts.filter { vkPostIds.contains(it.id.toInt()).not() }
        deletedPosts.forEach {
            logger.info(" -- Remove: $it")
            postRepo.delete(it)
        }

        return deletedPosts
    }

    private fun postToMap(post: Post) = mapOf(
            "id" to post.id,
            "link" to vkApiService.getPostLink(post.id),
            "statusId" to post.statusId,
            "from" to post.from.firstAndLastName,
            "date" to post.date,
            "text" to post.text,
            "startSum" to post.startSum,
            "distance" to post.distance,
            "sumDistance" to post.sumDistance
    )

}