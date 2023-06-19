package com.example.oauth2process.oauth2.handler

import com.example.oauth2process.jwt.service.JwtService
import com.example.oauth2process.oauth2.userinfo.CustomOAuth2User
import com.example.oauth2process.user.Role
import com.example.oauth2process.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class OAuth2LoginSuccessHandler(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
): AuthenticationSuccessHandler {
    companion object {
        private val log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler::class.java)
    }

    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        log.info("OAuth2 Login Success!")

        val oAuth2User = authentication?.principal as CustomOAuth2User?

        if (oAuth2User == null) return
        else {
            if (oAuth2User.role == Role.GUEST) {
                val accessToken = jwtService.createAccessToken(oAuth2User.email)
                val findUser = userRepository.findByEmail(oAuth2User.email)

                response?.addHeader(jwtService.accessHeader, "Bearer $accessToken")
                response?.sendRedirect("oauth2/sign-up")

                if (findUser == null) throw IllegalArgumentException("이메일에 해당하는 유저를 찾을 수 없음")
                else {
                    findUser.authorizaUser()
                    jwtService.sendAccessAndRefreshToken(response!!, accessToken, "")
                    userRepository.flush()
                }
            }

            else {
                val accessToken = jwtService.createAccessToken(oAuth2User.email)
                val refreshToken = jwtService.createRefreshToken()

                response?.addHeader(jwtService.accessHeader, "Bearer $accessToken")
                response?.addHeader(jwtService.refreshHeader, "Bearer $refreshToken")

                jwtService.sendAccessAndRefreshToken(response!!, accessToken, refreshToken)
                jwtService.updateRefreshToken(oAuth2User.email, refreshToken)
            }
        }
    }
}