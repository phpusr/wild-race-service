package com.phpusr.wildrace.service

import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EnvironmentService(private val environment: Environment) {

    fun isDevelopment(): Boolean {
        val activeProfiles = environment.activeProfiles
        return activeProfiles.contains("dev") || !activeProfiles.contains("prod")
    }

}