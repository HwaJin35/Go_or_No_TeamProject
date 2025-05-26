package com.goorno.canigo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 이메일 인증 요청 DTO

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthRequestDTO {
	
	@NotBlank
	@Email
	private String email;
	
}