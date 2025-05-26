package com.goorno.canigo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
	
	// 토큰 문자열로 토큰 객체를 조회(사용자가 링크 클릭 시 검증)
	Optional<PasswordResetToken> findByToken(String token);
	
	// 사용자가 기존에 발급받은 토큰이 있는지 확인
	Optional<PasswordResetToken> findByUserId(Long userId);
}
