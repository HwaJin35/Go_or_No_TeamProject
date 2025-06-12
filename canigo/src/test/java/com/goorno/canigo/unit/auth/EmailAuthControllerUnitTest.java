package com.goorno.canigo.unit.auth;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorno.canigo.controller.auth.AuthController;
import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.service.auth.EmailAuthService;

@WebMvcTest(controllers = AuthController.class)
@ContextConfiguration(classes = AuthController.class) // EmailAuthController만
@AutoConfigureMockMvc(addFilters=false)	// 테스트를 위해 시큐리티 필터 제거
class EmailAuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailAuthService emailAuthService; // 서비스 Mock

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("이메일 인증 코드 발송 - 성공")
    void sendAuthCode() throws Exception {
        // given
        EmailAuthRequestDTO requestDTO = new EmailAuthRequestDTO("test@example.com");

        doNothing().when(emailAuthService).sendVerificationCode(requestDTO);

        // when & then
        mockMvc.perform(post("/api/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 이메일로 발송되었습니다."));
    }

    @Test
    @DisplayName("이메일 인증 코드 검증 - 성공")
    void verifyAuthCode() throws Exception {
        // given
        EmailVerifyDTO verifyDTO = new EmailVerifyDTO("test@example.com", "123456");

        doNothing().when(emailAuthService).verifyCode(verifyDTO);

        // when & then
        mockMvc.perform(post("/api/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("이메일 인증이 완료되었습니다."));
    }
}