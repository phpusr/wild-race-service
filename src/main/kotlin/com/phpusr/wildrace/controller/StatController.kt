package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.dto.StatDto
import com.phpusr.wildrace.enum.StatType
import com.phpusr.wildrace.service.StatService
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import java.beans.PropertyEditorSupport


@RestController
@RequestMapping("stat")
class StatController(private val statService: StatService) {

    @GetMapping
    @JsonView(Views.StatDtoREST::class)
    fun getData(
            @RequestParam type: StatType?,
            @RequestParam startRange: String?,
            @RequestParam endRange: String?
    ): StatDto {
        val stat = statService.calcStat(type, startRange, endRange)

        return stat
    }

    @InitBinder
    fun initBinder(webDataBinder: WebDataBinder) {
        webDataBinder.registerCustomEditor(StatType::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String?) {
                value = if (text != null && text != "") {
                    StatType.values().find{ it.ordinal == text.toInt() }
                } else null
            }
        })
    }

}