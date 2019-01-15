package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.data.ConfigRepo
import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.PostDto
import com.phpusr.wildrace.dto.PostDtoObject
import com.phpusr.wildrace.service.StatService
import com.phpusr.wildrace.service.SyncService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.function.BiConsumer

@RestController
@RequestMapping("post")
class PostController(
        private val postRepo: PostRepo,
        private val statService: StatService,
        private val tempDataRepo: TempDataRepo,
        private val configRepo: ConfigRepo,
        private val postSender: BiConsumer<EventType, PostDto>,
        private val syncService: SyncService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    @JsonView(Views.PostDtoREST::class)
    fun list(
            @PageableDefault(sort = ["date"], direction = Sort.Direction.DESC) pageable: Pageable,
            @RequestParam statusId: Int?,
            @RequestParam manualEditing: Boolean?
    ): Map<String, Any> {
        val page = postRepo.findAll(pageable, statusId, manualEditing)
        val config = configRepo.get()
        val list = page.content.map { PostDtoObject.create(it, config) }

        return mapOf("list" to list, "totalElements" to page.totalElements)
    }

    @GetMapping("getStat")
    fun getStat(): Map<String, Any> {
        val lastPost = statService.getOneRunning(Sort.Direction.DESC)

        return mapOf(
                "sumDistance" to (lastPost?.sumDistance ?: 0),
                "numberOfRuns" to (lastPost?.number ?: 0),
                "numberOfPosts" to postRepo.count()
        )
    }

    @GetMapping("getLastSyncDate")
    fun getLastSyncDate(): Long {
        return tempDataRepo.get().lastSyncDate.time
    }

    @GetMapping("{id}")
    @JsonView(Views.PostDtoREST::class)
    fun get(@PathVariable("id") post: Post): PostDto {
        return PostDtoObject.create(post)
    }

    @PutMapping("{id}")
    @JsonView(Views.PostDtoREST::class)
    fun update(@RequestBody postDto: PostDto): PostDto? {
        val post = postRepo.findById(postDto.id).orElseThrow{ RuntimeException("post_not_found") }
        logger.debug(">> Hand update post: ${post}")
        post.number = postDto.number
        post.statusId = postDto.statusId
        post.distance = postDto.distance
        post.sumDistance = postDto.sumDistance
        post.editReason = postDto.editReason
        post.lastUpdate = Date()
        postRepo.save(post)

        val updatePostDto = PostDtoObject.create(post, configRepo.get())
        postSender.accept(EventType.Update, updatePostDto)
        //TODO добавить параметр, который будет определять запуск
        syncService.updateNextPosts(post)

        return updatePostDto
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") post: Post): Long {
        logger.debug(">> Hand delete post: ${post}")
        postRepo.deleteById(post.id)
        postSender.accept(EventType.Remove, PostDtoObject.create(post))
        post.number = null
        //TODO добавить параметр, который будет определять запуск
        syncService.updateNextPosts(post)

        return post.id
    }

    @GetMapping("sync")
    fun sync() {
        syncService.syncPosts()
    }

}