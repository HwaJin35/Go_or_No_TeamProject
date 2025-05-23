package com.goorno.canigo.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.goorno.canigo.common.jwt.JwtAccessDeniedHandler;
import com.goorno.canigo.common.jwt.JwtAuthenticationEntryPoint;
import com.goorno.canigo.common.jwt.JwtAuthenticationFilter;
import com.goorno.canigo.service.user.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

// Spring Security에서 웹 요청에 대한 접근 제어 정책을 설정
// ex) 어떤 URL은 인증 없이 허용 or 인증 필요
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final UserDetailsServiceImpl userDetailsService;
	// 추후 OAuth 인증 사용시 별도 인증 필터 만들어 추가.
    
	// Spring Security 5.7 이후부터는
	// WebSecurityConfigurerAdapter를 사용하지 않고,
	// SecurityFilterChain을 직접 Bean으로 등록하는 방식이 권장
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
       	   	.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
       	   	.csrf().disable() // JWT 사용 시 CSRF 불필요
       	   	.sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않음 (JWT 기반)
            .authorizeHttpRequests() // 요청 경로에 따라 인증 여부 설정
                .requestMatchers(GET, "/api/places").permitAll()
                .requestMatchers(GET, "/api/places/categories").permitAll()
                .requestMatchers(POST, "/api/places").authenticated()
            	.requestMatchers(
            			"/api/auth/**",
            			"/api/users/check-email",
            			"/api/users/check-nickname",
                		"/api/users/signup",
                		"/api/login",				// 로그인, 회원가입, 인증, 중복확은 인증없이 허용
                		"/v3/api-docs/**",		// swagger URL은 인증없이 허용
                		"/swagger-ui/**",
                		"/swagger-ui.html",
                		"/actuator/**"			// actuator는 인증 없이 허용
                		).permitAll()
                .anyRequest().authenticated()
            .and()
            .exceptionHandling(exceptionHandling -> 
            	exceptionHandling
            		.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 커스텀 EntryPoint 적용 -> 인증 실패 처리
            		.accessDeniedHandler(jwtAccessDeniedHandler())			// 인가 실패 처리
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
           return http.build();
    }
    
    // CORS 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
    	CorsConfiguration config = new CorsConfiguration();
    	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    	
    	config.setAllowedOrigins(List.of("http://localhost:8080"));
    	config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    	config.setAllowedHeaders(List.of("*"));
    	config.setAllowCredentials(true);
    	
    	source.registerCorsConfiguration("/**", config);
    	return source;
    }
    
    // 인증 관리자(로그인 시 사용)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    	AuthenticationManagerBuilder builder = http
    			.getSharedObject(AuthenticationManagerBuilder.class);
    	builder.userDetailsService(userDetailsService)
    			.passwordEncoder(passwordEncoder());
    	return builder.build();
    }
    
    // 비밀번호 암호화를 위한 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    // 인가 예외(403) 핸들러
    @Bean
    public AccessDeniedHandler jwtAccessDeniedHandler() {
    	return new JwtAccessDeniedHandler();
    }
}