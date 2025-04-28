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
		// 더미 데이터 생성
        User user = User.builder()
                .email("dummy@example.com")  // 이메일 (null 허용)
                .password("password123")  // 비밀번호 (테스트용, 실제 시스템에서는 암호화 필요)
                .nickname("dummyUser")  // 닉네임
                .profileImageFile("profile.jpg")  // 프로필 이미지 파일 (예시)
                .authProvider(AuthProviderType.LOCAL)  // 로그인 방식 (LOCAL 또는 KAKAO)
                .status(Status.ACTIVE)  // 사용자 상태 (ACTIVE, INACTIVE, BANNED 등)
                .build();
        
        // 더미 데이터 저장
        userRepository.save(user);
        
        // DB에서 저장된 사용자 조회 및 출력
        System.out.println("DB에서 가져온 데이터: " + userRepository.findAll());
    }
	
	public User getDummyUser() {
		return userRepository.findByEmail("dummy@example.com")
				.orElseThrow(() -> new RuntimeException("User not found"));
	}
}
