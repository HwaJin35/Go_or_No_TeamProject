package com.goorno.canigo.integration.user;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

/**
 * UserController 통합 테스트
 * - 실제 애플리케이션을 띄운 상태에서 Controller ~ Service ~ Repository를 모두 테스트
 * - Mock 객체를 사용하지 않고 실제 동작하는지 검증
 * - DB와 상호작용이 발생하므로 @Transactional로 롤백 처리
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // test 환경으로 properties 세팅
@Transactional
@WithMockUser(username = "testuser@example.com")		// 테스트용 시큐리티 설정, 사용자 인증을 위한 Mock 주입
public class UserControllerIntegrationTest {
	@Autowired
	private UserRepository userRepository;
	
    // 가짜 HTTP 요청을 보내기 위한 객체
    @Autowired
    private MockMvc mockMvc;

    // JAVA 객체를 JSON 문자열로 변환하는 객체
    @Autowired
    private ObjectMapper objectMapper;

    private MockMultipartFile profileImage;
    
    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {    	
    	// 테스트 실행 전 DB 비우기
    	userRepository.deleteAll();
    	
    	profileImage = new MockMultipartFile(
        		"files",       
        		"test.jpg",                 // 파일 이름
        		"image/jpeg",               // MIME 타입
        		"test image content".getBytes(StandardCharsets.UTF_8) // 파일 내용
        	);
    	MimeMessage fakeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(fakeMessage);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("회원가입 통합 테스트")
    void registerUser() throws Exception {
        
        // 이메일 인증 코드 발송 요청
        EmailAuthRequestDTO sendDTO = new EmailAuthRequestDTO("testuser@example.com");

        mockMvc.perform(post("/api/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("인증 코드가 이메일로 발송되었습니다."));

        // 유저의 인증 코드(authToken) 직접 꺼냄
        User user = userRepository.findByEmail("testuser@example.com").orElseThrow();
        String authToken = user.getAuthToken();

        // 이메일 인증 요청
        EmailVerifyDTO verifyDTO = new EmailVerifyDTO("testuser@example.com", authToken);

        mockMvc.perform(post("/api/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string("이메일 인증이 완료되었습니다."));

        // isVerified가 true인지 검증
        User verifiedUser = userRepository.findByEmail("testuser@example.com").orElseThrow();
        assertTrue(verifiedUser.isVerified());
    	
        // when - 회원가입 API 호출
    	 mockMvc.perform(multipart("/api/users/signup")
                 .file(profileImage)
                 .param("email", "testuser@example.com")
                 .param("password", "password123")
                 .param("nickname", "testnickname")
                 .param("authProvider", "LOCAL"))
        // then - 정상적으로 회원가입이 되어 200 OK 응답과 함께 nickname 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testnickname"));
    }

    @Test
    @DisplayName("전체 회원 조회 통합 테스트")
    void getAllUsers() throws Exception {
        // given - 회원가입
        registerUser();

        // when - 전체 회원 조회 API 호출
        mockMvc.perform(get("/api/users"))

        // then - 등록된 회원이 포함된 리스트 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"));
    }

    @Test
    @DisplayName("이메일로 회원 조회 통합 테스트")
    void getUserByEmail() throws Exception {
        // given
    	registerUser();
    	
        // when
        mockMvc.perform(get("/api/users/email/{email}", "testuser@example.com"))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testnickname"));
    }

    @Test
    @DisplayName("닉네임으로 회원 조회 통합 테스트")
    void getUserByNickname() throws Exception {
        // given
    	registerUser();
    	
        // when
        mockMvc.perform(get("/api/users/nickname/{nickname}", "testnickname"))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    @DisplayName("회원 정보 수정 통합 테스트")
    void updateUser() throws Exception {
    	registerUser();

        // 수정용 가짜 파일
        MockMultipartFile updatedProfileImage = new MockMultipartFile(
                "files",
                "updated.jpg",
                "image/jpeg",
                "updated image content".getBytes(StandardCharsets.UTF_8)
        );
        
        // when
        mockMvc.perform(multipart("/api/users/me")
                .file(updatedProfileImage)
                .with(req -> { req.setMethod("PUT"); return req; }) // multipart 기본 POST -> PUT으로 강제 변경
                .param("nickname", "updatedNickname")
            )
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("updatedNickname"));
    }

    @Test
    @DisplayName("회원 비활성화 통합 테스트")
    void deactivateUser() throws Exception {
    	registerUser(); // 인증 + 가입까지 수행
    	Long id = userRepository.findByEmail("testuser@example.com").orElseThrow().getId();
    	
        // when - 해당 ID로 비활성화 API 호출
        mockMvc.perform(patch("/api/users/{id}/deactivate", id))
        // then - 정상적으로 204 No Content가 나오는지 검증
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("회원 삭제 통합 테스트")
    void deleteUser() throws Exception {
    	registerUser(); // 인증 + 가입

        // when - 해당 ID로 삭제 API 호출
        mockMvc.perform(delete("/api/users/me"))
        // then - 정상적으로 204 No Content가 나오는지 검증
                .andExpect(status().isNoContent());
    }
}