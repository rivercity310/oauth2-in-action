package com.example.oauth2process.user.controller

import com.example.oauth2process.user.dto.UserSignUpDto
import com.example.oauth2process.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/user")
@RestController
class UserController(private val userService: UserService) {
    @PostMapping("/sign-up")
    fun signUp(@RequestBody userSignUpDto: UserSignUpDto): String {
        userService.signUp(userSignUpDto)
        return "회원가입 성공"
    }

    @GetMapping("/jwt-test")
    fun jwtTest(): String {
        return "JWT TEST 요청 성공"
    }
}