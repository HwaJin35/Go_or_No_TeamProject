package com.goorno.canigo.common.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

// JWT 생성, 검증 유틸

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtProperties jwtProperties;
	private Key key;

	// 객체가 초기화될 때 키를 셋팅
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    // AccessToken 생성
    public String createAccessToken(Long userId, String email, String nickname, String role) {
        Date now = new Date();
        long accessTokenExpireTime = jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000; // 분 → 밀리초 변환
        Date expiryDate = new Date(System.currentTimeMillis() + accessTokenExpireTime);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("roles", role)
                .claim("nickname", nickname)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // RefreshToken 생성
    public String createRefreshToken(Long userId, String email) {
    	long refreshTokenExpireTime = jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000;
    	Date expiryDate = new Date(System.currentTimeMillis() + refreshTokenExpireTime);
        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // 토큰에서 정보 추출
    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
    public long getUserIdFromClaim(String token) {
    	 Claims claims = parseClaims(token);
    	    return claims.get("userId", Number.class).longValue();
    	}

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
