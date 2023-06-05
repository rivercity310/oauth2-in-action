package com.example.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.web.SecurityFilterChain
import java.util.UUID

// https://www.baeldung.com/spring-security-oauth-auth-server
@Configuration
@Import(OAuth2AuthorizationServerConfiguration::class)
class AuthServerConfig {
    @Bean
    internal fun registeredClientRepository(): RegisteredClientRepository {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("test-client")
            .clientSecret("secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("http://localhost:9090/home")
            .scope("read")
            .build()

        val registeredClient2 = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("test-client2")
            .clientSecret("secret2")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("read")
            .redirectUri("http://localhost:9090/home")
            .build()

        val rcr = InMemoryRegisteredClientRepository(registeredClient, registeredClient2)
        return rcr
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    internal fun authServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        return http.formLogin(Customizer.withDefaults()).build()
    }
}