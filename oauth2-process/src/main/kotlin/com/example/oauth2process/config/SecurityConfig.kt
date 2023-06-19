package com.example.oauth2process.config

import com.example.oauth2process.jwt.filter.JwtAuthenticationProcessingFilter
import com.example.oauth2process.jwt.service.JwtService
import com.example.oauth2process.login.filter.CustomJsonUsernamePasswordAuthenticationFilter
import com.example.oauth2process.login.handler.LoginFailureHandler
import com.example.oauth2process.login.handler.LoginSuccessHandler
import com.example.oauth2process.login.service.LoginService
import com.example.oauth2process.oauth2.handler.OAuth2LoginFailureHandler
import com.example.oauth2process.oauth2.handler.OAuth2LoginSuccessHandler
import com.example.oauth2process.oauth2.service.CustomOAuth2UserService
import com.example.oauth2process.user.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val loginService: LoginService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
    private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler,
    private val customOAuth2UserService: CustomOAuth2UserService
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .formLogin { it.disable() }
                .httpBasic { it.disable() }
                .csrf { it.disable() }
                .headers { it.frameOptions { it.disable() }}
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

                .authorizeHttpRequests {
                    it.requestMatchers("/", "/css/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
                    it.requestMatchers("/sign-up").permitAll()
                    it.anyRequest().authenticated()
                }

                .oauth2Login {
                    it.successHandler(oAuth2LoginSuccessHandler)
                    it.failureHandler(oAuth2LoginFailureHandler)
                    it.userInfoEndpoint { it.userService(customOAuth2UserService) }
                }

                .addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter::class.java)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun jwtAuthenticationProcessingFilter(): JwtAuthenticationProcessingFilter {
        return JwtAuthenticationProcessingFilter(jwtService, userRepository)
    }

    @Bean
    fun customJsonUsernamePasswordAuthenticationFilter(): CustomJsonUsernamePasswordAuthenticationFilter {
        val filter = CustomJsonUsernamePasswordAuthenticationFilter(objectMapper)
        filter.setAuthenticationManager(authenticationManager())
        filter.setAuthenticationSuccessHandler(loginSuccessHandler())
        filter.setAuthenticationFailureHandler(loginFailureHandler())

        return filter
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(loginService)
        return ProviderManager(provider)
    }

    @Bean
    fun loginSuccessHandler(): LoginSuccessHandler {
        return LoginSuccessHandler(jwtService, userRepository)
    }

    @Bean
    fun loginFailureHandler(): LoginFailureHandler {
        return LoginFailureHandler()
    }
}