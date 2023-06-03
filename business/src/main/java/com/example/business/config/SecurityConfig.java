package com.example.business.config;

import com.example.business.auth.OtpAuthenticationProvider;
import com.example.business.auth.UsernamePasswordAuthenticationProvider;
import com.example.business.filter.InitialAuthenticationFilter;
import com.example.business.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/* Security 3.0 이후 authenticationManager 등록 https://backendstory.com/spring-security-how-to-replace-websecurityconfigureradapter/ */
@Configuration
public class SecurityConfig {
    @Autowired private OtpAuthenticationProvider otpAuthenticationProvider;
    @Autowired private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void registerProvider(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(otpAuthenticationProvider)
                .authenticationProvider(usernamePasswordAuthenticationProvider);
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAt(new InitialAuthenticationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();
    }
}
