package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.Config
import com.phpusr.wildrace.service.ConfigService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("config")
class ConfigController(private val configService: ConfigService) {

    @GetMapping
    fun get() = configService.get()

    @PutMapping
    fun update(@RequestBody config: Config): Config {
        configService.update(config)
        return config
    }

}