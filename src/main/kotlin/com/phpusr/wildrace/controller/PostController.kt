package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.data.ConfigRepo
import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.domain.vk.PostRepo
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("post")
class PostController(
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val configRepo: ConfigRepo
) {

    @GetMapping
    @JsonView(Views.PostREST::class)
    fun list(
            @PageableDefault(sort = ["date"], direction = Sort.Direction.DESC) pageable: Pageable,
            @RequestParam statusId: Int?,
            @RequestParam manualEditing: Boolean?
    ): Map<String, Any> {
        val page = postRepo.findAll(pageable, statusId, manualEditing)
        val lastPost = postRepo.findLastPost()

        return mapOf(
                "total" to page.totalElements,
                "totalPages" to page.totalPages,
                "lastSyncDate" to tempDataRepo.get().lastSyncDate,
                "sumDistance" to (lastPost.sumDistance ?: 0),
                "numberOfRuns" to (lastPost.number ?: 0),
                "config" to configRepo.get(),
                "list" to page.content
        )
    }

}