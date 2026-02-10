package com.tinytotrend.common;

import com.tinytotrend.auth.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtFilter jwtFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Static resources - must be first
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()
                
                // Error page
                .requestMatchers("/error").permitAll()
                
                // Public HTML pages
                .requestMatchers("/", "/index.html", "/login.html", "/register.html").permitAll()
                .requestMatchers("/product-detail.html", "/wishlist.html", "/cart.html", "/checkout.html", "/profile.html").permitAll()
                
                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()
                
                // Public product APIs
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                
                // Admin pages: allow GET so the browser can load the dashboard HTML (client-side JS enforces auth)
                .requestMatchers(HttpMethod.GET, "/admin/**").permitAll()
                // Admin APIs: require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Protected API endpoints - require authentication
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/wishlist/**").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                
                // All other requests permitted
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
