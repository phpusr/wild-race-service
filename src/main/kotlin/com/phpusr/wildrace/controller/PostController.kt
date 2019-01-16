package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Post
import com.phpusr.wildrace.domain.PostRepo
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.PostDto
import com.phpusr.wildrace.dto.PostDtoObject
import com.phpusr.wildrace.service.ConfigService
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
        private val configService: ConfigService,
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
        val config = configService.get()
        val list = page.content.map { PostDtoObject.create(it, config) }

        return mapOf("list" to list, "totalElements" to page.totalElements)
    }

    @GetMapping("{id}")
    @JsonView(Views.PostDtoREST::class)
    fun get(@PathVariable("id") post: Post): PostDto {
        return PostDtoObject.create(post)
    }

    @PutMapping("{id}")
    @JsonView(Views.PostDtoREST::class)
    fun update(@RequestBody postDto: PostDto, @RequestParam(defaultValue = "false") updateNextPosts: Boolean): PostDto {
        val post = postRepo.findById(postDto.id).orElseThrow{ RuntimeException("post_not_found") }
        logger.debug(">> Hand update post: $post")
        post.apply {
            number = postDto.number
            statusId = postDto.statusId
            distance = postDto.distance
            sumDistance = postDto.sumDistance
            editReason = postDto.editReason
            lastUpdate = Date()
        }
        postRepo.save(post)

        val updatePostDto = PostDtoObject.create(post, configService.get())
        postSender.accept(EventType.Update, updatePostDto)
        if (updateNextPosts) {
            syncService.updateNextPosts(post)
        }

        return updatePostDto
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable("id") post: Post, @RequestParam(defaultValue = "false") updateNextPosts: Boolean): Long {
        logger.debug(">> Hand delete post: ${post}")
        postRepo.deleteById(post.id)
        postSender.accept(EventType.Remove, PostDtoObject.create(post))
        post.number = null
        if (updateNextPosts) {
            syncService.updateNextPosts(post)
        }

        return post.id
    }

    @GetMapping("sync")
    fun sync() {
        syncService.syncPosts()
    }

}