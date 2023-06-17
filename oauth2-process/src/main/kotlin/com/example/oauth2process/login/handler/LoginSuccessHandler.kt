package com.example.oauth2process.login.handler

import com.example.oauth2process.jwt.service.JwtService
import com.example.oauth2process.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class LoginSuccessHandler(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    @Value("\${jwt.access.expiration}") private val accessTokenExpiration: String
): SimpleUrlAuthenticationSuccessHandler() {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(LoginSuccessHandler::class.java)
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val email = extractUsername(authentication)
        val accessToken = jwtService.createAccessToken(email)
        val refreshToken = jwtService.createRefreshToken()

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken)

        val user = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("해당 이메일을 찾을 수 없음")

        user.updateRefreshToken(refreshToken)
        userRepository.saveAndFlush(user)

        log.info("로그인 성공!\n이메일 : {}\nAccessToken : {}\n토큰 만료일 : {}", email, accessToken, accessTokenExpiration)
    }

    private fun extractUsername(authentication: Authentication): String {
        val userDetails = authentication.principal as UserDetails
        return userDetails.username
    }
}