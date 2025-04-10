package com.goorno.canigo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

// Spring Security에서 웹 요청에 대한 접근 제어 정책을 설정
// ex) 어떤 URL은 인증 없이 허용 or 인증 필요

@Configuration
public class SecurityConfig {
	
	// Spring Security 5.7 이후부터는
	// WebSecurityConfigurerAdapter를 사용하지 않고,
	// SecurityFilterChain을 직접 Beand로 등록하는 방식이 권장
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.cors(Customizer.withDefaults()) // CORS를 기본 설정으로 활성화
            .csrf().disable() // CSRF 보호 기능을 끈다.(JWT 사용 시 일반적으로 끔)
            .authorizeHttpRequests() // 요청 경로에 따라 인증 여부 설정
                .requestMatchers("/api/**").permitAll() // API는 인증 없이 허용
                .anyRequest().authenticated()
            .and()
            .formLogin(); // 기본 로그인 폼 사용

        return http.build();
    }
}