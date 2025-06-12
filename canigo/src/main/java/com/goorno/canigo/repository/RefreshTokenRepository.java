package com.goorno.canigo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	Optional<RefreshToken> findByUser_Id(Long userId);
    
    void deleteByUser_Id(Long userId);

	Optional<RefreshToken> findByToken(String refreshToken);
}
