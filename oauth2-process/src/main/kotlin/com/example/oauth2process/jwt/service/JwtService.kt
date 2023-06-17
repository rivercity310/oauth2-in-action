package com.example.oauth2process.jwt.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.oauth2process.user.User
import com.example.oauth2process.user.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secretKey}") private val secretKey: String,
    @Value("\${jwt.access.expiration}") private val accessTokenExpirationPeriod: Long,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpirationPeriod: Long,
    @Value("\${jwt.access.header}") private val accessHeader: String,
    @Value("\${jwt.refresh.header}") private val refreshHeader: String,
    private val userRepository: UserRepository
) {
    companion object {
        private val log = LoggerFactory.getLogger(JwtService::class.java)
        private const val ACCESS_TOKEN_SUBJECT = "AccessToken"
        private const val REFRESH_TOKEN_SUBJECT = "RefreshToken"
        private const val EMAIL_CLAIM = "email"
        private const val BEARER = "Bearer "
    }

    /* AccessToken 생성 메서드 */
    fun createAccessToken(email: String): String {
        val now = Date()

        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + accessTokenExpirationPeriod))
            .withClaim(EMAIL_CLAIM, email)
            .sign(Algorithm.HMAC512(secretKey))
    }

    /* RefreshToken 생성 메서드 */
    fun createRefreshToken(): String {
        val now = Date()

        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(Date(now.time + refreshTokenExpirationPeriod))
            .sign(Algorithm.HMAC512(secretKey))
    }

    /* AccessToken 헤더에 추가 */
    fun sendAccessToken(res: HttpServletResponse, accessToken: String) {
        res.status = HttpServletResponse.SC_OK
        res.setHeader(accessHeader, accessToken)
        log.info("재발급된 Access Token : {}", accessToken)
    }

    /* AccessToken + RefreshToken 헤더에 추가 */
    fun sendAccessAndRefreshToken(res: HttpServletResponse, accessToken: String, refreshToken: String) {
        res.status = HttpServletResponse.SC_OK
        res.setHeader(accessHeader, accessToken)
        res.setHeader(refreshHeader, refreshToken)
        log.info("Access Token, Refresh Token 헤더 설정 완료")
    }

    /* 요청 헤더에서 AccessToken 추출 */
    fun extractAccessToken(req: HttpServletRequest): String? {
        val acc = req.getHeader(accessHeader)

        return if (acc.startsWith(BEARER)) acc.replace(BEARER, "")
        else null
    }

    /* 요청 헤더에서 RefreshToken 추출 */
    fun extractRefreshToken(req: HttpServletRequest): String? {
        val ref = req.getHeader(refreshHeader)

        return if (ref.startsWith(BEARER)) ref.replace(BEARER, "")
        else null
    }

    /* AccessToken에서 Email 추출 */
    fun extractEmail(accessToken: String): String? {
        return try {
                JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString()
        } catch (e: Exception) {
            log.error("[이메일 추출 실패] 엑세스 토큰이 유효하지 않습니다.")
            null
        }
    }

    /* RefreshToken DB 저장 (업데이트) */
    fun updateRefreshToken(email: String, refreshToken: String) {
        val user: User = userRepository.findByEmail(email) ?: throw Exception("일치하는 회원이 없습니다.")
        user.updateRefreshToken(refreshToken)
    }

    /* 토큰 유효성 검사 */
    fun isTokenValid(token: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token)
            true
        } catch (e: Exception) {
            log.error("유효하지 않은 토큰입니다. {}", e.message)
            false
        }
    }
}