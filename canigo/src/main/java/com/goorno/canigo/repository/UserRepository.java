package com.goorno.canigo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;

public interface UserRepository extends JpaRepository<User, Long> {

	// 이메일로 회원 조회
	Optional<User> findByEmail(String email);
	
	// 상태로 회원 조회
	List<User> findByStatus(Status status);
}
