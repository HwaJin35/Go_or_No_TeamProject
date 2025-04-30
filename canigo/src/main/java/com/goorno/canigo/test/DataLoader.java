package com.goorno.canigo.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private final UserRepository userRepository;
	
	@Transactional
	@Override
	public void run(String... args) throws Exception {
		String dummyEmail = "dummy@example.com";

	    // 이미 존재하는지 체크
	    boolean exists = userRepository.findByEmail(dummyEmail).isPresent();
		
	    if (!exists) {
	        User user = User.builder()
	            .email(dummyEmail)
	            .password("password123")
	            .nickname("dummyUser")
	            .profileImageBase64("profile.jpg")
	            .authProvider(AuthProviderType.LOCAL)
	            .status(Status.ACTIVE)
	            .build();
	        
	        userRepository.save(user);
	        System.out.println("Dummy user 저장 완료!");
        
	    } else {
	        System.out.println("Dummy user 이미 존재함. 저장 생략");
	    }
	}
	public User getDummyUser() {
		return userRepository.findByEmail("dummy@example.com")
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}