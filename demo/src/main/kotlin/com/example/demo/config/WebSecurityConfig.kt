package com.example.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/* 사용자 관리에 필요한 구성 작성 */
@EnableWebSecurity
@Configuration
class WebSecurityConfig {
    @Bean
    internal fun userDetailsService(): UserDetailsService {
        val uds = InMemoryUserDetailsManager()

        val user = User
            .withUsername("seungsu")
            .password("12345")
            .authorities("read")
            .build()

        uds.createUser(user)
        return uds
    }

    @Bean
    internal fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    internal fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {  it.anyRequest().authenticated() }
            .formLogin(Customizer.withDefaults())
            .build()
    }
}