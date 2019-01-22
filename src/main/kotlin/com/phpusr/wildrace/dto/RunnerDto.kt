package com.phpusr.wildrace.dto

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Profile
import com.phpusr.wildrace.domain.Views

@JsonView(Views.StatDtoREST::class)
class RunnerDto(
        val profile: Profile,
        val numberOfRuns: Int,
        val sumDistance: Int
)