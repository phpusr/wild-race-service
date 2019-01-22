package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.TempDataRepo
import com.phpusr.wildrace.service.EnvironmentService
import com.phpusr.wildrace.service.StatService
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Transactional(readOnly = true)
@RequestMapping("/")
@Controller
class MainController(
        private val tempDataRepo: TempDataRepo,
        private val statService: StatService,
        private val environmentService: EnvironmentService
) {

    @GetMapping
    fun main(
            model: Model,
            @AuthenticationPrincipal user: UserDetails?,
            @RequestParam forceProd: Boolean = false
    ): String {
        val isProdMode = forceProd || environmentService.isProduction
        val host = if (isProdMode) "/" else "http://192.168.1.100:8000/"

        model["isProdMode"] = isProdMode
        model["jsFiles"] = if (isProdMode) getFiles("js", "js", host) else listOf("${host}app.js")
        model["cssFiles"] = if (isProdMode) getFiles("css", "css", host) else listOf()

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

    private fun getFiles(dir: String, ext: String, host: String): List<String> {
        return ClassPathResource("static/$dir")
                .file
                .listFiles()
                .filter{ it.name.endsWith(ext) }
                .map{ "$host$dir/${it.name}" }
    }


}