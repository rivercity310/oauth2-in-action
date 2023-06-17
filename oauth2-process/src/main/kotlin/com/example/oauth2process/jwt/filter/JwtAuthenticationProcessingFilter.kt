package com.example.oauth2process.jwt.filter

import com.example.oauth2process.jwt.service.JwtService
import com.example.oauth2process.user.User
import com.example.oauth2process.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/* "/login" 이외 요청을 담당하는 필터 */
class JwtAuthenticationProcessingFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository
): OncePerRequestFilter() {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(JwtAuthenticationProcessingFilter::class.java)
        private val authoritiesMapper = NullAuthoritiesMapper()
        private const val NO_CHECK_URL = "/login"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI.equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response)
            return
        }

        val refreshToken = jwtService.extractRefreshToken(request) ?: return

        // RefreshToken이 유효하면 AccessToken을 재발급
        if (jwtService.isTokenValid(refreshToken)) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken)
            return
        }

        // RefreshToken이 유효하지 않으면 AccessToken을 검사
        // AccessToken이 유효하지 않으면 403, 유효하면 인증 성공 (인증 객체가 담긴채로 다음 필터로 넘어감)
        checkAccessTokenAndAuthentication(request, response, filterChain)
    }

    private fun checkRefreshTokenAndReIssueAccessToken(response: HttpServletResponse, refreshToken: String) {
        val user = userRepository.findByRefreshToken(refreshToken) ?: return
        val reIssuedRefreshToken = reIssueRefreshToken(user)
        jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.email!!), reIssuedRefreshToken)
    }

    private fun reIssueRefreshToken(user: User): String {
        val reIssuedRefreshToken = jwtService.createRefreshToken()
        user.updateRefreshToken(reIssuedRefreshToken)
        userRepository.saveAndFlush(user)
        return reIssuedRefreshToken
    }

    private fun checkAccessTokenAndAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info("checkAccessTokenAndAuthentication() 호출")

        val accessToken = jwtService.extractAccessToken(request) ?: return

        if (jwtService.isTokenValid(accessToken)) {
            val email = jwtService.extractEmail(accessToken) ?: return
            val user = userRepository.findByEmail(email) ?: return
            saveAuthentication(user)
        }

        filterChain.doFilter(request, response)
    }

    private fun saveAuthentication(user: User) {
        val password = user.password ?: UUID.randomUUID().toString()

        val userDetailsUser = org.springframework.security.core.userdetails.User.builder()
            .username(user.email!!)
            .password(password)
            .roles(user.role!!.name)
            .build()

        val authentication = UsernamePasswordAuthenticationToken(
            userDetailsUser,
            null,
            authoritiesMapper.mapAuthorities(userDetailsUser.authorities)
        )

        SecurityContextHolder.getContext().authentication = authentication
    }
}