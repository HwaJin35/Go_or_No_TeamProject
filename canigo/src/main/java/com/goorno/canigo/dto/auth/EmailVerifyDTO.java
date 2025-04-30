package com.goorno.canigo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 이메일 인증 확인 DTO

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyDTO {
	
	private String email;
	private String code;
	
}