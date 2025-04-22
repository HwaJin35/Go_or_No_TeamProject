package com.goorno.canigo.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

@Service
public class AdminUserService {

	private final UserRepository userRepository;
	
	@Autowired
	public AdminUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	// 회원 목록 조회
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	// 회원 상태 수정 (활성화/비활성화/차단/삭제 등)
	public void updateUserStatus(Long userId, Status status, Integer banDurationDays) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setStatus(status);
			
			// 차단 상태인 경우
			if (status == Status.BANNED) {
				user.setBanStartDate(LocalDateTime.now());
				user.setBanEndDate(LocalDateTime.now().plusDays(banDurationDays));
				user.setBanDurationDays(banDurationDays);
			} else {
				user.setBanStartDate(null);
				user.setBanEndDate(null);
				user.setBanDurationDays(null);
			}
			userRepository.save(user); // 상태 변경 후 저장
		}
	}
	
	// 차단 기간 만료 체크
	public void checkBanStatus() {
		List<User> bannedUsers = userRepository.findByStatus(Status.BANNED);
		for (User user : bannedUsers) {
			if (user.getBanEndDate().isBefore(LocalDateTime.now())) {
				user.setStatus(Status.ACTIVE); // 차단 해제
				user.setBanStartDate(null);
				user.setBanEndDate(null);
				userRepository.save(user);
			}
		}
	}
	
	// 회원 탈퇴 처리
	public void deleteUser(Long userId) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isPresent()) {
			userRepository.delete(userOptional.get()); // 회원 삭제
		}
	}
}
