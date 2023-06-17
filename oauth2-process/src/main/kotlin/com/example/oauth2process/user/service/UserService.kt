package com.example.oauth2process.user.service

import com.example.oauth2process.user.Role
import com.example.oauth2process.user.User
import com.example.oauth2process.user.dto.UserSignUpDto
import com.example.oauth2process.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signUp(userSignUpDto: UserSignUpDto) {
        if (userRepository.findByEmail(userSignUpDto.email).isPresent) {
            throw Exception("이미 존재하는 Email")
        }

        if (userRepository.findByNickname(userSignUpDto.nickname).isPresent) {
            throw Exception("이미 존재하는 Nickname")
        }

        val user = User(
            email = userSignUpDto.email,
            password = passwordEncoder.encode(userSignUpDto.password),
            nickname = userSignUpDto.nickname,
            age = userSignUpDto.age,
            city = userSignUpDto.city,
            role = Role.USER,
        )

        userRepository.save(user)
    }
}