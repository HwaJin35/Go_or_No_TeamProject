package com.goorno.canigo.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private final UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		// 데이터 삽입
		User user = new User();
		user.setName("홍길동");
		userRepository.save(user);
		
		// 데이터 조회
		System.out.println("DB에서 가져온 데이터: " + userRepository.findAll());
	}
}
