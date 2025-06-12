package com.goorno.canigo.service.auth;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goorno.canigo.common.jwt.JwtUtil;
import com.goorno.canigo.common.util.HashUtil;
import com.goorno.canigo.entity.RefreshToken;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.RefreshTokenRepository;
import com.goorno.canigo.service.user.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    
    // 리프레시 토큰을 검증하고 새로운 액세스 토큰 발급
    @Transactional
    public ResponseEntity<?> reissueAccessToken(String refreshToken, HttpServletResponse response) {
        // 토큰 유효성 확인
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 리프레시 토큰입니다.");
        }
        System.out.println("@@@@@@@@@@ Raw token from cookie: " + refreshToken);
        System.out.println("@@@@@@@@@@ Hash token from cookie: " + HashUtil.sha256(refreshToken));
        
        // 비교용 해시 토큰 생성
        String hashedToken = HashUtil.sha256(refreshToken);
        // 저장된 리프레시 토큰 확인
        RefreshToken savedToken = refreshTokenRepository.findByToken(hashedToken).orElse(null);
        if (savedToken == null || savedToken.isExpired()) {
            return ResponseEntity.status(401).body("토큰이 만료되었거나 없습니다.");
        }

        // 유효한 경우, 사용자 조회 후 AccessToken 재발급
        Long userId = jwtUtil.getUserIdFromClaim(refreshToken);
        User user = userService.getUserOrThrow(userId);
        
        // 새 accessToken과 refreshToken 발급
        String newAccessToken = jwtUtil.createAccessToken(
            user.getId(), user.getEmail(), user.getNickname(), user.getRole().toString()
        );
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail());
        // refreshToken 암호화 -> 이걸 DB에 저장!
        String newHashedToken = HashUtil.sha256(newRefreshToken);
        
        // 기존 RefreshToken 엔티티가 있을 경우 덮어 씀
        savedToken.setToken(newHashedToken);
        savedToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(savedToken);
        
        // 새 refreshToken을 HttpOnly 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
        	    .httpOnly(true)
        	    .secure(false) // 개발환경
        	    .sameSite("Strict")
        	    .path("/")
        	    .maxAge(7 * 24 * 60 * 60)
        	    .build();

        	response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
    
    // 로그인 시 RefreshToken 저장
    public void saveRefreshToken(String token, User user, long expireDays) {
    	String hashedToken = HashUtil.sha256(token); // 토큰 암호화
    	RefreshToken refreshToken = new RefreshToken(
    			hashedToken,
    			user,
    			LocalDateTime.now().plusDays(expireDays)
    	);
    	refreshTokenRepository.save(refreshToken);
    }
    
    // Cookie로부터 refreshToken 추출
    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}