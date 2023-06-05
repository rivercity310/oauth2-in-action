package com.example.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class ProjectConfig {
    @Bean
    internal fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.oauth2Login { it.clientRegistrationRepository(clientRepository()) }
        http.authorizeHttpRequests { it.anyRequest().authenticated() }

        return http.build()
    }

    internal fun clientRepository(): ClientRegistrationRepository {
        val clientRegistration = clientRegistration()
        return InMemoryClientRegistrationRepository(clientRegistration)
    }

    private fun clientRegistration(): ClientRegistration {
        return CommonOAuth2Provider.GITHUB
            .getBuilder("github")
            .clientId("8342b623388853d9b655")
            .clientSecret("2756b99d209a8a424ef9a3f8d037f12b16f92e36")
            .build()
    }
}