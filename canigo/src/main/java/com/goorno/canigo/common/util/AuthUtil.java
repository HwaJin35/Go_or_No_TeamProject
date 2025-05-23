package com.goorno.canigo.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtil {

	private final UserRepository userRepository;
	
	public User getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication()
				.getName();
		
		return userRepository.findByEmail(email)
				.filter(user -> user.isVerified() && user.getStatus() == Status.ACTIVE)
				.orElseThrow(() -> new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다."));
	}
}
