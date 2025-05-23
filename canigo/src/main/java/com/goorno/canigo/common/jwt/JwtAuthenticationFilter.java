package com.goorno.canigo.common.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// JWT 인증 필터(매 요청마다 실행)
// Authorization 헤더에서 토큰 추출
// 유효한 경우 인증 정보 SecurityContext에 저장
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 토큰 유효성 검사
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 정보 추출
                Claims claims = jwtUtil.parseClaims(token);
                String email = claims.getSubject();
                
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                	var userDetails = userDetailsService.loadUserByUsername(email);
                	
                	// 인증 객체 생성
                	UsernamePasswordAuthenticationToken authenticationToken =
                			new UsernamePasswordAuthenticationToken(
                					userDetails, // principal = UserDetails 객체
                					null,  // credentials (비밀번호 필요 없음)
                					userDetails.getAuthorities() // 권한 목록
                					);
                	// SecurityContext에 인증 정보 저장
                	SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        // 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}
