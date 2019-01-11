package com.phpusr.wildrace.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.consts.Consts
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.data.Config
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.Profile

@JsonView(Views.PostDtoREST::class)
class PostDto(
        val id: Long,
        val number: Int?,
        val statusId: Int,
        val from: Profile?,
        val date: Long?,
        val text: String?,
        val distance: Int?,
        val sumDistance: Int?,
        val editReason: String?,
        val link: String?
)

object PostDtoObject {
    fun create(post: Post): PostDto {
        return PostDto(post.id, post.number, post.statusId, null, null, null, post.distance, post.sumDistance, post.editReason, null)
    }
    fun create(post: Post, config: Config): PostDto {
        val link = "${Consts.VKLink}/${config.groupShortLink}?w=wall${config.groupId}_${post.id}"
        return PostDto(post.id, post.number, post.statusId, post.from, post.date.time, post.text, post.distance, post.sumDistance, post.editReason, link)
    }
}