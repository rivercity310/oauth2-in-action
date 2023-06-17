package com.example.oauth2process.user

enum class Role(val key: String) {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
    ;
}