package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
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
class PostController(private val postRepo: PostRepo, private val tempDataRepo: TempDataRepo) {

    @GetMapping
    @JsonView(Views.PostREST::class)
    fun list(
            @PageableDefault(sort = ["date"], direction = Sort.Direction.DESC) pageable: Pageable,
            @RequestParam statusId: Int?,
            @RequestParam manualEditing: Boolean?
    ): Map<String, Any> {
        val page = postRepo.findAll(pageable, statusId, manualEditing)
        val list = page.content
        val lastSyncDate = tempDataRepo.get().lastSyncDate
        val lastPost = postRepo.findLastPost()
        val sumDistance = lastPost.sumDistance ?: 0
        val numberOfRuns = lastPost.number ?: 0

        return mapOf("total" to page.totalElements, "list" to list, "lastSyncDate" to lastSyncDate,
                "sumDistance" to sumDistance, "numberOfRuns" to numberOfRuns)
    }

}