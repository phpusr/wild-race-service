package com.phpusr.wildrace.domain.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.vk.Profile
import java.util.*

@JsonView(Views.PostDtoREST::class)
class PostDto(
        val id: Long,
        val number: Int?,
        val from: Profile,
        val date: Date,
        val text: String,
        val distance: Int?,
        val sumDistance: Int?,
        val link: String
)