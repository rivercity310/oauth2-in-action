package com.example.oauth2process.user.repository

import com.example.oauth2process.user.SocialType
import com.example.oauth2process.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByNickname(nickname: String): Optional<User>
    fun findByRefreshToken(refreshToken: String): Optional<User>
    fun findBySocialTypeAndSocialId(socialType: SocialType, socialId: String): Optional<User>
}