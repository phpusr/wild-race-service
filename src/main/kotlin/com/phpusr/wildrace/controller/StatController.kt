package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.domain.dto.StatDto
import com.phpusr.wildrace.service.StatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("stat")
class StatController(private val statService: StatService) {

    @GetMapping
    @JsonView(Views.StatDtoREST::class)
    fun getData(
            @RequestParam typeForm: String,
            @RequestParam startRange: String?,
            @RequestParam endRange: String?
    ): StatDto {
        val stat = statService.calcStat(typeForm, startRange, endRange)

        return stat
    }

}