package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.data.Config
import com.phpusr.wildrace.domain.data.ConfigRepo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("config")
class ConfigController(private val configRepo: ConfigRepo) {

    @GetMapping
    fun get(): Config {
        return configRepo.get()
    }

}