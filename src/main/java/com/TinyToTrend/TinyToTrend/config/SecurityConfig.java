package com.TinyToTrend.TinyToTrend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration to allow API access for development
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
                .requestMatchers("/api/**") // Allow all API endpoints for now
                .permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
            .headers(headers -> headers
                .frameOptions().deny());
        
        return http.build();
    }
}
