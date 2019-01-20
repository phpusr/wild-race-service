package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.TempDataRepo
import com.phpusr.wildrace.service.StatService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Transactional(readOnly = true)
@RequestMapping("/")
@Controller
class MainController(
        private val tempDataRepo: TempDataRepo,
        private val statService: StatService
) {

    @GetMapping
    fun main(model: Model, @AuthenticationPrincipal user: UserDetails?): String {
        model["frontendData"] = mapOf(
                "user" to user,
                "stat" to statService.getStat(),
                "lastSyncDate" to tempDataRepo.get().lastSyncDate.time
        )
        return "index"
    }

    @GetMapping("authorize")
    fun authorize(request: HttpServletRequest, response: HttpServletResponse): String {
        return "authorize"
    }


}