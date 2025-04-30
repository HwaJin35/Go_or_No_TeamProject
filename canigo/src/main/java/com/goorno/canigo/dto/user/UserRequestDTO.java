package com.goorno.canigo.dto.user;

import com.goorno.canigo.common.dto.CommonMultipartDTO;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 유저 관련 요청 DTO
// 유저 회원가입 요청시
// 유저 정보 수정 요청시
// 비밀번호 검증/ 변경 요청시

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO extends CommonMultipartDTO {
	
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;
	
	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;
	
	private AuthProviderType authProvider;
	private Role role;
	private Status status;
	
}
