package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.data.Config
import com.phpusr.wildrace.domain.data.ConfigRepo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("config")
class ConfigController(private val configRepo: ConfigRepo) {

    @GetMapping
    fun get(): Config {
        return configRepo.get()
    }

    @PutMapping
    fun update(@RequestBody config: Config): Config {
        return configRepo.save(config)
    }

}