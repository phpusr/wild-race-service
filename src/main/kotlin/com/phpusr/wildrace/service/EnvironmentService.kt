package com.phpusr.wildrace.service

import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class EnvironmentService(private val environment: Environment) {

    private val ProdMode = "prod"
    private val DevMode = "dev"

    val isDevelopment: Boolean get() {
        val activeProfiles = environment.activeProfiles
        return activeProfiles.contains(DevMode) || !activeProfiles.contains(ProdMode)
    }

    val isProduction: Boolean get() {
        return environment.activeProfiles.contains(ProdMode)
    }

}