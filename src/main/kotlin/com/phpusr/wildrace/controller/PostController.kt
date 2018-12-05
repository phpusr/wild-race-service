package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.data.ConfigRepo
import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.dto.PostDto
import com.phpusr.wildrace.domain.dto.PostDtoObject
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("post")
class PostController(
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val configRepo: ConfigRepo
) {

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
        val lastPost = postRepo.findLastPost()

        return mapOf(
                "sumDistance" to (lastPost.sumDistance ?: 0),
                "numberOfRuns" to (lastPost.number ?: 0),
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

    @MessageMapping("/updatePost")
    @SendTo("/topic/updatePost")
    @JsonView(Views.PostDtoREST::class)
    fun update(postDto: PostDto): PostDto? {
        val post = postRepo.findById(postDto.id)
        val newPost = post.orElseThrow{ RuntimeException("post_not_found") }.copy(
                number = postDto.number,
                statusId = postDto.statusId,
                distance = postDto.distance,
                sumDistance = postDto.sumDistance,
                editReason = postDto.editReason,
                lastUpdate = Date()
        )
        postRepo.save(newPost)

        return PostDtoObject.create(newPost, configRepo.get())
    }

    @MessageMapping("/deletePost")
    @SendTo("/topic/deletePost")
    fun delete(id: Long): Long {
        postRepo.deleteById(id)

        return id
    }

}