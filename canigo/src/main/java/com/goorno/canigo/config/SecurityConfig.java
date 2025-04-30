package com.goorno.canigo.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.goorno.canigo.common.jwt.JwtAuthenticationEntryPoint;
import com.goorno.canigo.common.jwt.JwtAuthenticationFilter;
import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.service.user.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

// Spring Security에서 웹 요청에 대한 접근 제어 정책을 설정
// ex) 어떤 URL은 인증 없이 허용 or 인증 필요
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	// 추후 OAuth 인증 사용시 별도 인증 필터 만들어 추가.
    
	// Spring Security 5.7 이후부터는
	// WebSecurityConfigurerAdapter를 사용하지 않고,
	// SecurityFilterChain을 직접 Bean으로 등록하는 방식이 권장
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
        	.cors(Customizer.withDefaults()) // CORS를 기본 설정으로 활성화
            .csrf().disable() // CSRF 보호 기능을 끈다.(JWT 사용 시 일반적으로 끔)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음 (JWT 기반)
            .authorizeHttpRequests() // 요청 경로에 따라 인증 여부 설정
                .requestMatchers(GET, "/api/places").permitAll()
                .requestMatchers(GET, "/api/places/categories").permitAll()
                .requestMatchers(POST, "/api/places").authenticated()
            	.requestMatchers(
            			"/api/auth/**",
            			"/api/users/check-email",
                		"/api/users/signup",
                		"/api/login",				// 로그인, 회원가입, 인증, 이메일 중복은 인증없이 허용
                		"/v3/api-docs/**",		// swagger URL은 인증없이 허용
                		"/swagger-ui/**",
                		"/swagger-ui.html",
                		"/actuator/**"			// actuator는 인증 없이 허용
                		).permitAll()
                .anyRequest().authenticated()
            .and()
            .exceptionHandling(exceptionHandling -> 
            exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 커스텀 EntryPoint 적용
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class) // 🔥 JWT 필터 추가
       		.formLogin(Customizer.withDefaults());
           return http.build();
    }
    
    // 비밀번호 암호화를 위한 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}