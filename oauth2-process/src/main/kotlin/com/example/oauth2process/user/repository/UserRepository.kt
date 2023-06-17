package com.example.oauth2process.user.repository

import com.example.oauth2process.user.SocialType
import com.example.oauth2process.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByNickname(nickname: String): User?
    fun findByRefreshToken(refreshToken: String): User?
    fun findBySocialTypeAndSocialId(socialType: SocialType, socialId: String): User?
}