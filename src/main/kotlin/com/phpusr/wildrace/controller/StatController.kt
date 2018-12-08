package com.phpusr.wildrace.controller

import com.phpusr.wildrace.service.StatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("stat")
class StatController(private val statService: StatService) {

    @GetMapping
    fun getData(
            @RequestParam typeForm: Int,
            @RequestParam startDistance: Int?,
            @RequestParam endDistance: Int?,
            @RequestParam startDate: Long?,
            @RequestParam endDate: Long?
    ) {
        val sDate: Date?
        val eDate: Date?
        if (typeForm == 0) {
            sDate = if (startDistance != null) statService.getStartDate(startDistance) else null
            eDate = if (endDistance != null) statService.getEndDate(endDistance) else null
        } else {
            sDate = if (startDate != null) Date(startDate) else null
            // Изменение времени на окончание дня
            eDate = if (endDate != null) Date(endDate + 24 * 3600 * 1000 - 1) else null
        }

        val stat = statService.calcStat(sDate, eDate, startDistance, endDistance)

        return stat
    }

}