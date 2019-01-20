package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.Config
import com.phpusr.wildrace.service.ConfigService
import com.phpusr.wildrace.service.VKApiService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional(readOnly = true)
@RequestMapping("config")
@RestController
class ConfigController(
        private val configService: ConfigService,
        private val vkApiService: VKApiService
) {

    @GetMapping
    fun get(): Map<String, Any> {
        return mapOf(
                "config" to configService.get(),
                "authorizeUrl" to vkApiService.authorizeUrl
        )
    }

    @Transactional
    @PutMapping
    fun update(@RequestBody config: Config): Config {
        configService.update(config)
        return config
    }

}