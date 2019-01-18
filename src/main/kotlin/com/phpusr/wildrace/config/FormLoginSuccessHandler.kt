package com.phpusr.wildrace.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * src: https://www.baeldung.com/securing-a-restful-web-service-with-spring-security
 */
class FormLoginSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    private val requestCache: RequestCache = HttpSessionRequestCache()

    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse,
                                         authentication: Authentication) {

        response.contentType = "application/json"
        response.writer.apply {
            write(ObjectMapper().writeValueAsString(authentication.principal))
            flush()
        }

        val savedRequest = requestCache.getRequest(request, response)
        if (savedRequest == null) {
            clearAuthenticationAttributes(request)
            return
        }

        val targetUrlParam = targetUrlParameter
        if (isAlwaysUseDefaultTargetUrl || targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam))) {
            requestCache.removeRequest(request, response)
            clearAuthenticationAttributes(request)
            return
        }

        clearAuthenticationAttributes(request)
    }

}