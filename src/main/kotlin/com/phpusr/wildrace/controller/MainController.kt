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
    fun main(model: Model, @AuthenticationPrincipal user: UserDetails?): String {
        model["isProdMode"] = environmentService.isProduction
        model["jsFiles"] = getFiles("js", "js")
        model["cssFiles"] = getFiles("css", "css")

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

    private fun getFiles(dir: String, ext: String): List<String> {
        return ClassPathResource("static/$dir")
                .file
                .listFiles()
                .filter{ it.name.endsWith(ext) }
                .map{ it.name }
    }


}