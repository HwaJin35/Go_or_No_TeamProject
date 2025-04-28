package com.goorno.canigo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Status;

public interface UserRepository extends JpaRepository<User, Long> {

	// 이메일로 회원 조회
	Optional<User> findByEmail(String email);
	
	// 닉네임로 회원 조회
	Optional<User> findByNickname(String Nickname);
	
	// 이메일 + 로그인 방식으로 조회
	Optional<User> findByEmailAndAuthProvider(String email, AuthProviderType provider);
	
	// 상태로 회원 조회
	List<User> findByStatus(Status status);
	
	// 닉네임 중복 확인
	boolean existsByNickname(String nickname);
	
	// 이메일 중복 확인
	boolean existsByEmail(String email);
	
	// 닉네임으로 검색(관리자용)
	List<User> findByNicknameContainingIgnoreCase(String keyword);
}
