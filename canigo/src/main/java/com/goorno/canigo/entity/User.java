package com.goorno.canigo.entity;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.enums.AuthProviderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 이메일 (nullable: 카카오 로그인 시 없을 수도 있음)
	@Column(nullable = true, unique = true)
	private String email;
	
	// 비밀번호 (일반 로그인 사용자만 사용, 카카오 로그인은 null 허용)
	// 비밀번호는 BCrypt로 암호화해서 저장
	// @@@ 비밀번호 확인 추가
	private String password;
	
	// 닉네임
	@Column(nullable = false, unique = true)
	private String nickname;
	
	// 프로필 이미지
	// DTO 추가?
	private String profileImageUrl;
	
	// 로그인 방식 (LOCAL, KAKAO)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuthProviderType authProvider;
	
	// 활성화 여부 (차단이나 탈퇴 등)
	private boolean active = true;
}
