package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.data.TempDataRepo
import com.phpusr.wildrace.service.StatService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class MainController(
        private val tempDataRepo: TempDataRepo,
        private val statService: StatService
) {

    @GetMapping
    fun main(model: Model): String {
        model["frontendData"] = mapOf(
                "stat" to statService.getStat(),
                "lastSyncDate" to tempDataRepo.get().lastSyncDate.time
        )
        return "index"
    }

}