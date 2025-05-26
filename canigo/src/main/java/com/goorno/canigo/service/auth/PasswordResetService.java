package com.goorno.canigo.service.auth;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.goorno.canigo.common.exception.ErrorCode;
import com.goorno.canigo.common.exception.UserException;
import com.goorno.canigo.dto.auth.PasswordResetConfirmDTO;
import com.goorno.canigo.entity.PasswordResetToken;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.repository.PasswordResetTokenRepository;
import com.goorno.canigo.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
	
	private final UserRepository userRepository;
	private final PasswordResetTokenRepository tokenRepository;
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${app.reset-password-url}")
	String resetUrl;

	// 비밀번호 재설정 링크 발송 메서드
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        // 기존 토큰이 있다면 삭제
        tokenRepository.findByUserId(user.getId()).ifPresent(tokenRepository::delete);
        // 새 토큰 생성
        String token = UUID.randomUUID().toString();
        // 토큰 만료 시간
        int expirationMinutes = 10; 
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();
        tokenRepository.save(resetToken);
        // 이메일 링크
        String resetLink = resetUrl + token;

        // 타임리프 Context에 값 주입 및 본문 구성
        Context context = new Context();
        context.setVariable("nickname", user.getNickname());
        context.setVariable("resetLink", resetLink);
        context.setVariable("expirationMinutes", expirationMinutes);

        // 템플릿 엔진으로 HTML 렌더링
        String htmlContent = templateEngine.process("reset-password", context);

        // HTML 메일 전송
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
            );

            helper.setTo(user.getEmail());
            helper.setSubject("[여기 가도 될까?] 비밀번호 재설정 안내");
            helper.setText(htmlContent, true); // ✅ HTML 사용 설정

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송에 실패했습니다.", e);
        }
    }
    
    // 비밀번호 재설정 메서드
    public void resetPassword(PasswordResetConfirmDTO confirmDTO) {
    	// 토큰 여부 검사
        PasswordResetToken resetToken = tokenRepository.findByToken(confirmDTO.getToken())
                .orElseThrow(() -> new UserException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken); // 토큰 만료 시 삭제
            throw new UserException(ErrorCode.EXPIRED_PASSWORD_RESET_TOKEN);
        }

        // 사용자 조회
        User user = resetToken.getUser();

        // 새 비밀번호 유효성 검사
        if (!confirmDTO.getNewPassword().equals(confirmDTO.getConfirmPassword())) {
            throw new UserException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        if (confirmDTO.getNewPassword().length() < 8) {
            throw new UserException(ErrorCode.PASSWORD_TOO_SHORT);
        }

        if (passwordEncoder.matches(confirmDTO.getNewPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.PASSWORD_DUPLICATE);
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(confirmDTO.getNewPassword()));

        // 저장 및 토큰 삭제
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}