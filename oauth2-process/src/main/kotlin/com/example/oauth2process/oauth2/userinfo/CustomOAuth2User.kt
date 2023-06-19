package com.example.oauth2process.oauth2.userinfo

import com.example.oauth2process.user.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

data class CustomOAuth2User(
        val userAuthorities: Collection<GrantedAuthority>,
        val userAttributes: Map<String, Any>,
        val nameAttributeKey: String,
        val email: String,
        val role: Role
): DefaultOAuth2User(userAuthorities, userAttributes, nameAttributeKey)
