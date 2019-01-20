package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.Config
import com.phpusr.wildrace.service.ConfigService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional(readOnly = true)
@RequestMapping("config")
@RestController
class ConfigController(private val configService: ConfigService) {

    @GetMapping
    fun get() = configService.get()

    @Transactional
    @PutMapping
    fun update(@RequestBody config: Config): Config {
        configService.update(config)
        return config
    }

}