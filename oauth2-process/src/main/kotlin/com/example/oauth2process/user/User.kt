package com.example.oauth2process.user

import jakarta.persistence.*
import org.springframework.security.crypto.password.PasswordEncoder

@Table(name = "USERS")
@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var email: String? = null,
    var password: String? = null,
    var nickname: String? = null,
    var imageUrl: String? = null,
    var age: Int? = null,
    var city: String? = null,
    var socialId: String? = null,
    var refreshToken: String? = null,

    @Enumerated(EnumType.STRING)
    var role: Role? = null,

    @Enumerated(EnumType.STRING)
    var socialType: SocialType? = null
) {
    fun authorizaUser() {
        this.role = Role.USER
    }

    fun updateRefreshToken(updateRefreshToken: String) {
        this.refreshToken = refreshToken
    }
}