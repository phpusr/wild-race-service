package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.data.ConfigRepo
import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.domain.vk.Profile
import com.phpusr.wildrace.domain.vk.ProfileRepo
import com.phpusr.wildrace.util.Util
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class SyncService(
        private val configRepo: ConfigRepo,
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val profileRepo: ProfileRepo,
        private val vkApiService: VKApiService
) {

    fun syncPosts() {
        if (!configRepo.get().syncPosts) {
            return
        }

        val DownloadCountPosts = 100
        var needSync = true
        while(needSync) {
            val countPosts = getCountPosts()
            val alreadyDownloadCount = postRepo.count()
            println(">> Download: ${alreadyDownloadCount}/${countPosts}")
            if (needSync) {
                syncBlockPosts(countPosts, alreadyDownloadCount, DownloadCountPosts)
                needSync = alreadyDownloadCount < countPosts
                Thread.sleep(1000)
            }
        }

        tempDataRepo.updateLastSyncDate()
    }

    private fun syncBlockPosts(countPosts: Long, alreadyDownloadCount: Long, downloadCount: Int) {
        val offset = if (countPosts - alreadyDownloadCount > downloadCount) {
            countPosts - alreadyDownloadCount - downloadCount
        } else 0

        val profileList = profileRepo.findAll()

        val result = vkApiService.wallGet(offset, downloadCount, true)
        val response = result!!["response"] as Map<*, *>

        if (response["count"] as Long != countPosts) {
            return
        }

        val posts = (response["items"] as List<*>).reversed()
        //removeDeletedPosts(posts)

        postRepo.findRunningPage(PageRequest.of(0, 200, Sort(Sort.Direction.DESC, "date")))
        val profiles = response["profiles"] as List<*>

        posts.forEach {
            val postMap = it as Map<*, *>
            val postId = postMap["id"] as Long
            val text = postMap["text"] as String
            val textHash = Util.MD5(text)
            val postOp = postRepo.findById(postId)
            var post: Post? = null
            var isUpdate = false

            if (postOp.isPresent) {
                post = postOp.get()
                if (textHash == post.textHash || post.lastUpdate != null) {
                    return
                }

                isUpdate = true
                println(">> Post change: ${post}")
            } else {
                val postDate = Date(postMap["date"] as Long * 1000)
                // Поиск или создание профиля пользователя
                val profile = findOrCreateProfile(postMap, profileList, profiles, postDate)
                post = Post(from = profile)
                post.id = postId
                post.date = postDate
            }

            //TODO continue
        }
    }

    private fun findOrCreateProfile(postMap: Map<*, *>, profileList: MutableIterable<Profile>, profiles: List<*>, postDate: Date): Profile? {
        val profileId = postMap["from_id"] as Long
        var profile = profileList.find{ it.id == profileId }
        if (profile == null) {
            val profileMap = profiles.find{
                val map = it as Map<*, *>
                map["id"] == profileId
            } as Map<*, *>
            profile = Profile()
            profile.id = profileId
            profile.joinDate = postDate
            profile.firstName = profileMap["first_name"] as String?
            profile.lastName = profileMap["lastName"] as String?
            profile.sex = profileMap["sex"] as Int?
            profile.photo_50 = profileMap["photo_50"] as String?
            profile.photo_100 = profileMap["photo_100"] as String?
            profileRepo.save(profile)
            profileList.plus(profile)
        }

        return profile
    }

    private fun getCountPosts(): Long {
        val result = vkApiService.wallGet(0, 1, false)

        if (result == null) {
            throw Exception("Response is null")
        }

        return (result["response"] as Map<*, *>)["count"] as Long
    }

}