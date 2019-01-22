package com.phpusr.wildrace.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Post
import com.phpusr.wildrace.domain.Profile
import com.phpusr.wildrace.domain.Views

@JsonView(Views.PostDtoREST::class)
class PostDto(
        val id: Long,
        val number: Int?,
        val statusId: Int,
        val from: Profile?,
        val date: Long?,
        val text: String?,
        val distance: Short?,
        val sumDistance: Long?,
        val lastUpdate: Long?,
        val editReason: String?,
        val link: String?
)

object PostDtoObject {
    fun create(post: Post): PostDto {
        return PostDto(post.id, post.number, post.statusId, null, null, null, post.distance,
                post.sumDistance, post.lastUpdate?.time, post.editReason, null)
    }
    fun create(post: Post, getPostLink: (Long) -> String): PostDto {
        val link = getPostLink(post.id)
        return PostDto(post.id, post.number, post.statusId, post.from, post.date.time, post.text, post.distance,
                post.sumDistance, post.lastUpdate?.time, post.editReason, link)
    }
}