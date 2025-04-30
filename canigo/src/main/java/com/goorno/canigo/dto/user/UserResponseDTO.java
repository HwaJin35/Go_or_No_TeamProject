package com.goorno.canigo.dto.user;

import java.time.LocalDateTime;

import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 유저 정보 응답용 DTO
// 보안을 위해 비밀번호는 포함하지 않음 -> 서비스 계층에서 처리
// 로그인 후 사용자 정보 반환

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
	
	private Long id;
	private String email;
	private String nickname;
	private String profileImageFile;		// Base64로 인코딩된 프로필 이미지
	private AuthProviderType authProvider;
	private Status status;
	private boolean isVerified;
	private LocalDateTime banStartDate;
	private LocalDateTime banEndDate;
	private Integer banDurationDays;
	private Role role;
	private String aboutMe;

}
