package com.goorno.canigo.unit.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.goorno.canigo.admin.service.AdminUserService;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test") // "test" 프로파일을 활성화 application-test.properties
public class AdminUserServiceTest {

	@Autowired
	private AdminUserService adminUserService;
	
	@Autowired
	private UserRepository userRepository;
	
	// this 사용을 위해 전역 변수화
	Long userId;
	
	@BeforeEach	// 각 테스트 메서드가 실행되기 전에
				// 실행될 메서드를 지정하는 역할. 즉, 초기화 작업이나 설정 작업
	public void setUp() {
		// 임의 데이터 추가
		// 매번 테스트 전에 데이터베이스를 초기화
	    userRepository.deleteAll(); // 기존 데이터 삭제
	    
		User user1 = User.builder()
				.email("testuser1@example.com")
				.nickname("testuser1")
				.password("password1") // 실제로는 BCrypt로 암호화 필요
				.profileImageFile("profile1.png")
				.authProvider(AuthProviderType.LOCAL)
				.status(Status.ACTIVE)
				.build();
		
		User newUser = userRepository.save(user1);
		
		// 전역 변수로 Id 사용
		this.userId = newUser.getId();
	}
	
	@Test
	public void testUpdateUserStatus() {
		// 해당 이메일을 찾음
		User user = userRepository.findByEmail("testuser1@example.com")
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		
		// 사용자의 상태를 변경하고, 변경된 상태를 DB에 반영
		adminUserService.updateUserStatus(user.getId(), Status.BANNED, 3);
		
		// DB에서 다시 가져와 상태를 확인
		User updatedUser = userRepository.findById(user.getId())
				.orElseThrow(() -> new IllegalArgumentException("업데이트 후 사용자를 찾을 수 없습니다."));
		
		// 사용자의 상태가 업데이트 되었는지 비교함
		assertEquals(Status.BANNED, updatedUser.getStatus());
	}
	
	@Test
	public void testDeleteUserByEmail() {
//		// 해당 이메일을 찾음
//		User user = userRepository.findByEmail("testuser1@example.com")
//				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		
		// 해당 Id 찾음
		User user = userRepository.findById(userId).orElseThrow();
		
		// 사용자 삭제
		adminUserService.deleteUser(user.getId());
		
		// 삭제된 후 사용자가 존재하지 않는지 확인
		assertFalse(userRepository.existsById(user.getId())); // 존재하지 않으면 False 반환
		
	}
}
