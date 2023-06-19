package com.example.oauth2process.oauth2.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginFailureHandler: AuthenticationFailureHandler {
    companion object {
        private val log = LoggerFactory.getLogger(OAuth2LoginFailureHandler::class.java)
    }

    override fun onAuthenticationFailure(request: HttpServletRequest?, response: HttpServletResponse?, exception: AuthenticationException?) {
        response?.status = HttpServletResponse.SC_BAD_REQUEST
        response?.writer?.write("소셜 로그인 실패!")
        log.warn("소셜 로그인 실패!! 에러 메세지 : {}", exception?.message)
    }
}