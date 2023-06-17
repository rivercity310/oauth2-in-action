package com.example.oauth2process.user.dto

data class UserSignUpDto(
    val email: String,
    val password: String,
    val nickname: String,
    val city: String,
    val age: Int
)