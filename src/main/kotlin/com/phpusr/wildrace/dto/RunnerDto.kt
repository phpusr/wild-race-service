package com.phpusr.wildrace.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.vk.Profile

@JsonView(Views.StatDtoREST::class)
class RunnerDto(
        val profile: Profile,
        val numberOfRuns: Long,
        val sumDistance: Long
)