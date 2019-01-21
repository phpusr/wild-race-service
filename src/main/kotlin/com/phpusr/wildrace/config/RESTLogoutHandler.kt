package com.phpusr.wildrace.config

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RESTLogoutHandler(httpStatusToReturn: HttpStatus?) : HttpStatusReturningLogoutSuccessHandler(httpStatusToReturn) {

    override fun onLogoutSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        if (request!!.method != HttpMethod.POST.name) {
            response!!.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }

        super.onLogoutSuccess(request, response, authentication)
    }
}