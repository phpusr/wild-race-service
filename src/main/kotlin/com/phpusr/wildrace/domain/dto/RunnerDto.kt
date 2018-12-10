package com.phpusr.wildrace.domain.dto

import com.phpusr.wildrace.domain.vk.Profile

class RunnerDto(
        val profile: Profile,
        val numberOfRuns: Long,
        val sumDistance: Long
)