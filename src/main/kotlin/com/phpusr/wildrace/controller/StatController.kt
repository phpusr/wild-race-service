package com.phpusr.wildrace.controller

import com.fasterxml.jackson.annotation.JsonView
import com.phpusr.wildrace.domain.Views
import com.phpusr.wildrace.dto.StatDto
import com.phpusr.wildrace.enum.StatTypeOfForm
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
            @RequestParam typeOfForm: StatTypeOfForm?,
            @RequestParam startRange: String?,
            @RequestParam endRange: String?
    ): StatDto {
        val stat = statService.calcStat(typeOfForm, startRange, endRange)

        return stat
    }

    @InitBinder
    fun initBinder(webDataBinder: WebDataBinder) {
        webDataBinder.registerCustomEditor(StatTypeOfForm::class.java, object : PropertyEditorSupport() {
            override fun setAsText(text: String?) {
                value = if (text != null && text != "") {
                    StatTypeOfForm.values().find{ it.ordinal == text.toInt() }
                } else null
            }
        })
    }

}