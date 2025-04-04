package com.goorno.canigo.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/api/**").permitAll() // API는 인증 없이 허용
                .anyRequest().authenticated()
            .and()
            .formLogin(); // 기본 로그인 폼 사용

        return http.build();
    }
}