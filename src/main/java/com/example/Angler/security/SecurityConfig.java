package com.example.Angler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())           // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()          // 모든 요청 인증 없이 허용
                );
        return http.build();
    }
}