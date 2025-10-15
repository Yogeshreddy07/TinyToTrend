package com.TinyToTrend.TinyToTrend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Temporary security configuration for Milestone 0
 * Will be properly implemented in Milestone 4
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/static/**", "/*.html", "/css/**", "/js/**", "/images/**")
                .permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // Temporary for development
            .headers(headers -> headers.frameOptions().deny());
        
        return http.build();
    }
}

