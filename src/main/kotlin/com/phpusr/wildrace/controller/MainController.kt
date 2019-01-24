package com.phpusr.wildrace.controller

import com.phpusr.wildrace.domain.TempDataRepo
import com.phpusr.wildrace.service.EnvironmentService
import com.phpusr.wildrace.service.StatService
import com.phpusr.wildrace.service.VKApiService
import org.slf4j.LoggerFactory
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
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
        private val environmentService: EnvironmentService,
        private val vkApiService: VKApiService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun main(
            model: Model,
            @AuthenticationPrincipal user: UserDetails?,
            @RequestParam prod: Boolean = false
    ): String {
        val isProdMode = prod || environmentService.isProduction
        logger.info(">> PRODUCTION MODE: $isProdMode")
        val host = if (isProdMode) "/" else "http://192.168.1.100:8000/"

        model["isProdMode"] = isProdMode
        model["jsFiles"] = if (isProdMode) getFiles("js", "js", host) else listOf("${host}app.js")
        model["cssFiles"] = if (isProdMode) getFiles("css", "css", host) else listOf()

        model["frontendData"] = mapOf(
                "user" to user,
                "stat" to statService.getStat(),
                "lastSyncDate" to tempDataRepo.get().lastSyncDate.time,
                "config" to mapOf(
                    "groupLink" to vkApiService.groupLink
                )
        )
        return "index"
    }

    @GetMapping("authorize")
    fun authorize(request: HttpServletRequest, response: HttpServletResponse): String {
        return "authorize"
    }

    private fun getFiles(dir: String, ext: String, host: String): List<String> {
        return PathMatchingResourcePatternResolver(javaClass.classLoader)
                .getResources("classpath*:/static/$dir/*.$ext")
                .map{ "$host$dir/${it.filename}" }
    }


}