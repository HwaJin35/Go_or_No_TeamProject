package com.goorno.canigo.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// 이메일로 사용자 정보를 조회
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다."));
		
		// User의 role(ENUM)을 문자열로 변환
		String role = user.getRole().name();
		
		// User 엔터티를 Spring Security가 이해할 수 있는 UserDetails 객체로 변환
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPassword())
				.roles(role)
				.build();
	}
}
