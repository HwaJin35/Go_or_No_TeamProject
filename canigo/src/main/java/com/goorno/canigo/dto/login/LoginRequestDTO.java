package com.goorno.canigo.dto.login;

import com.goorno.canigo.entity.enums.AuthProviderType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 요청 DTO

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
	
	private AuthProviderType authProvider;	// LOCAL, KAKAO
	
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;
	
	
}
