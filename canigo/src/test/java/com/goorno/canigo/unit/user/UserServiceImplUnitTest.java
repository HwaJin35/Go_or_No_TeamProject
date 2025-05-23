package com.goorno.canigo.unit.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.mapper.user.UserMapper;
import com.goorno.canigo.repository.UserRepository;
import com.goorno.canigo.service.user.UserServiceImpl;

// UserServiceImpl 단위테스트
// 모든 Mokito 설정(mock 동작)이 예상대로 작동하고, 최종 결과가 assert 구문을 통과하면 성공
// -> 단위 테스트이므로 DB에 접근하지 않고, Mock 객체로 동작 검증
@ExtendWith(MockitoExtension.class)		// @BeforeEach에서 @Mock, @InjectMocks 객체들을 자동 초기화
public class UserServiceImplUnitTest {

	// @Mock - 테스트 대상 클래스가 의존하고 있는 외부 객체를 가짜(Mock)로 만들어줌.
	@Mock   
	private UserRepository userRepository;	// DB와 연결되지 않음

	@Mock
	private UserMapper userMapper;	

	@Mock
	private PasswordEncoder passwordEncoder;	// 실제 비밀번호를 암호화 하지 않고 결과만 설정

	// @InjectMocks - 테스트할 대상 클래스에 @Mock으로 만든 객체들을 주입
	@InjectMocks
	private UserServiceImpl userService;

	private UserRequestDTO requestDTO;
	private UserResponseDTO responseDTO;
	private User user;
	private MockMultipartFile mockFile;

	// 테스트 전에 공통으로 실행할 준비 코드
	@BeforeEach
	void setUp() {
		// 테스트용 가짜 파일 생성	
		mockFile = new MockMultipartFile(
				"profileImage",
				"test.jpg",
				"image/jpeg",
				"mockImageContent".getBytes(StandardCharsets.UTF_8)
				);
		
		// base64 형식의 프로필 이미지 문자열 세팅
        String profileImageBase64 = "data:image/jpeg;base64,dummybase64string";
        
		// 테스트용 유저 엔터티 객체
		user = User.builder()
				.id(1L)
				.email("test@example.com")
				.password("testPassword")
				.nickname("tester")
				.authProvider(AuthProviderType.LOCAL)
				.role(Role.USER)
				.build();
		
		// 회원가입 요청 DTO
		requestDTO = UserRequestDTO.builder()
				.email("test@example.com")
				.password("testPassword")
				.nickname("tester")
				.authProvider(AuthProviderType.LOCAL)
				.role(Role.USER)
				.build();
		requestDTO.setUploadFiles(List.of(mockFile));
		
		// 응답용 DTO
		responseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .profileImageFile(profileImageBase64)
                .authProvider(AuthProviderType.LOCAL)
				.role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }
		
		@Test
	    @DisplayName("회원가입 테스트")
	    void createUser_success() {
	        // given : 가정 상황 설정
			// when(____).thenReturn(___): Mock 객체가 특정 동작을 했을 때, 어떤 값을 리턴할지 결정
			// Mokito - any() : 어떤 값이든 허용  **테스트에서 값보다 메서드 호출 여부가 중요할 때 사용
	        when(userRepository.existsByNickname("tester")).thenReturn(false);  // 닉네임 중복 없음
	        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);	// 이메일 중복 없ㅇ므
	        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");	// 비밀번호 암호화 가정
	        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(user);  // 요청 DTO -> Entity 변환
	        when(userRepository.save(any(User.class))).thenReturn(user);  // 저장된 엔터티 반환
	        when(userMapper.toResponseDTO(any(User.class))).thenReturn(responseDTO);  // Entity ->  응답 DTO 변환
	
	        // when : 실제 호출
	        UserResponseDTO result = userService.createUser(requestDTO);
	
	        // then : 결과 검증
	        assertThat(result).isNotNull();		// 응답 객체가 null이 아님
	        assertThat(result.getEmail()).isEqualTo("test@example.com"); // 이메일이 예상과 일치
	        assertThat(result.getNickname()).isEqualTo("tester"); // 닉네임이 예상과 일치
	        
	        // verify(___) : 해당 메서드가 실제로 호출되었는지 검증
	        verify(userRepository).existsByNickname("tester");
	        verify(userRepository).existsByEmail("test@example.com");
	        verify(passwordEncoder).encode("testPassword");
	        verify(userRepository).save(any(User.class));
	    }
	
	    @Test
	    @DisplayName("닉네임 중복 예외 테스트")
	    void createUser_fail_nicknameExists() {
	    	// given: 닉네임이 이미 존재하는 경우
	        when(userRepository.existsByNickname("tester")).thenReturn(true);   // 닉네임이 이미 존재
	        
	        // when & then : 예외가 발생하는지 확인
	        // assertThorws( ___ ) : 특정 예외가 발생하는지 검증
	        assertThrows(IllegalArgumentException.class, () -> {
	            userService.createUser(requestDTO);
	        });
	    }
	    
	    @Test
	    @DisplayName("이메일 중복 예외 테스트")
	    void createUser_fail_emailExists() {
	    	// given : 이메일이 이미 존재하는 경우
	    	when(userRepository.existsByEmail("test@example.com")).thenReturn(true); // 이메일이 이미 존재
	    	
	    	// when & then : 예외가 발생하는지 확인
	    	assertThrows(IllegalArgumentException.class, () -> {
	    		userService.createUser(requestDTO);
	    	});
	    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void getUserByEmail_success() {
    	// 이메일을 찾으면 유저 엔티티 객체를 반환(Null이 아니어야 함)하고, 그것을 응답 dto로 변환 -> 이메일이 존재하고 있다.
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);
        
        // 실제 호출
        UserResponseDTO result = userService.getUserByEmail("test@example.com");
        
        // 예상값과 실제 값이 일치하는지 확인
        assertThat(result.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));  // id로 유저를 찾음

        userService.deleteUser(1L);  // 탈퇴 시도

        verify(userRepository).delete(user);  // delete가 실제로 호출되었는지 확인
    }

    @Test
    @DisplayName("회원정보 수정 테스트")
    void updateUser_success() {
        // given
    	// 수정할 회원정보 요청 DTO
        UserUpdateRequestDTO updatedRequestDTO = UserUpdateRequestDTO.builder()
                .nickname("updatedNickname")
                .build();
        updatedRequestDTO.setUploadFiles(List.of(mockFile));

        // 수정 전 기존에 저장된 유저를 가정
        User existingUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("tester")
                .authProvider(AuthProviderType.LOCAL)
                .status(Status.ACTIVE)
                .build();
        
        // userRepository.findById() 호출 시 기존 유저를 반환하도록 설정(DB 조회 시뮬레이션)
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        
        // updateUserFromDTO 호출 시 아무 일도 하지 않도록 설정(수정 흐름만 검증)
        doNothing().when(userMapper).updateUserFromDTO(any(User.class), any(UserUpdateRequestDTO.class));
        // userMapper.toResponseDTO() 호출 시 미리 준비한 responseDTO 변환(응답 흐름만 검증)
        when(userMapper.toResponseDTO(any())).thenReturn(responseDTO); 

        // when(테스트 대상 메서드 호출)
        // 실제로 서비스 메서드를 호출(수정 흐름 실행)
        UserResponseDTO result = userService.updateUser(1L, updatedRequestDTO);

        // then(검증)
        assertThat(result).isNotNull();		// 정상 응답 반환 확인
        verify(userRepository).findById(1L);	// findById 호출 검증
        verify(userMapper).updateUserFromDTO(any(User.class), any(UserUpdateRequestDTO.class));	// updateUserFromDTO() 호출 검증
        verify(userMapper).toResponseDTO(any());	// 수정된 User를 응답DTO로 변환했는지 검증
    }
    
    @Test
    @DisplayName("회원 전체 조회 테스트")
    void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));  // 리스트 하나 반환
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);  // 응답 DTO로 반환

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);   // 리스트에 한 명만 있는지 검증
    }
}
