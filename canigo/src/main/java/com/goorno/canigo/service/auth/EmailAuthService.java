package com.goorno.canigo.service.auth;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.goorno.canigo.dto.auth.EmailAuthRequestDTO;
import com.goorno.canigo.dto.auth.EmailVerifyDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 인증 이메일 전송 및 코드 검증 서비스

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {
	
	private final JavaMailSender mailSender;
	private final UserRepository userRepository;
	private final TemplateEngine templateEngine;	// 타임리프 템플릿 엔진
	
	@Value("${spring.mail.username}")
	private String mailUsername;	// SMTP 설정 발신자 메일 주소
	
	// 인증 코드 전송 메서드
	// 아직 가입되지 않은 사용자에게만 허용
	@Transactional
	public void sendVerificationCode(EmailAuthRequestDTO requestDTO) {
		String email = requestDTO.getEmail();
		
	    // 이미 가입된 이메일이면 인증 거부
	    userRepository.findByEmail(email).ifPresent(existingUser -> {
	        if (existingUser.isVerified() && existingUser.getStatus() == Status.ACTIVE) {
	            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
	        }
	    });
		
		// 임시 사용자 조회(없다면 임시 생성)
		User user = userRepository.findByEmail(email)
			    .orElseGet(() -> {
			        User newUser = User.builder()
			            .email(email)
			            .nickname("guest_" + UUID.randomUUID().toString().substring(0, 8))	// 임시 닉네임
			            .authProvider(AuthProviderType.LOCAL)
			            .status(Status.PENDING)
			            .role(Role.USER)
			            .isVerified(false)
			            .build();
			        return userRepository.save(newUser);
			    });;
		
		// 6자리 인증 코드 생성
		String authCode = String.format("%06d",  new Random().nextInt(999999));
		
		// 유저 엔터티에 인증코드와 만료 시간 저장(3분 후 만료)
		int expirationMinutes = 3;
		user.setAuthToken(authCode);
		user.setAuthTokenExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
		userRepository.save(user);
		
		// 이메일 메시지 구성(타임리프 템플릿 이용)
		Context context = new Context();
		context.setVariable("nickname", user.getNickname());
		context.setVariable("authCode", authCode);
		context.setVariable("expirationMinutes",expirationMinutes);
		
		// resources/templates/email-verification.html
		String htmlContent = templateEngine.process("email-verification-template",  context);
		
        // 메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(mailUsername);
            helper.setSubject("[여기 가도 될까?] 이메일 인증 코드 안내메일입니다.");
            helper.setText(htmlContent, true); // true = HTML 형식

            mailSender.send(message);
            log.info("✅ 인증 코드 이메일 발송 완료! - 인증 대상: {}", email);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송에 실패했습니다", e);
        }
    }
	
	// 이메일 코드 검증 메서드
	@Transactional
	public void verityCode(EmailVerifyDTO verifyDTO) {
		String email = verifyDTO.getEmail();
		String inputCode = verifyDTO.getCode();
		
		// 사용자 조회
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		
		// 인증 요청 기록 확인
		if (user.getAuthToken() == null || user.getAuthTokenExpiresAt() == null) {
			throw new IllegalStateException("인증 요청 기록이 없습니다. 인증을 다시 요청해 주세요");
		}
		
		// 인증 코드 만료 검사
		if (user.getAuthTokenExpiresAt().isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("인증 코드가 만료되었습니다. 다시 요청해 주세요");
		}
		
		// 코드 일치 여부 검사
		if(!user.getAuthToken().equals(inputCode)) { 
			throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
		}
		
		// 인증 성공 처리
		user.setVerified(true);				// 인증 완료 플래그
		user.setAuthToken(null); 			// 토큰제거
		user.setAuthTokenExpiresAt(null);	// 만료시간 제거
		userRepository.save(user);
		
		log.info("✅이메일 인증 성공! 인증된 주소: {}", email);
	}
}
