package com.goorno.canigo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.entity.rankSystem.Rank;
import com.goorno.canigo.entity.rankSystem.UserActivity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`user`")
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
    @Lob
	private String profileImageBase64;
	
	// 로그인 방식 (LOCAL, KAKAO)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuthProviderType authProvider;
	
	// 인증여부
	// 로그인 방식이 LOCAL 이면 이메일 인증, KAKAO면 카카오 인증을 받은 후 true
	@Column(nullable = false)
	private boolean isVerified = false;
	
	// 등급
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
				orphanRemoval = true, fetch = FetchType.LAZY)
	private Rank rank;
	
	// 활동 기록
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserActivity> activityList = new ArrayList<>();
	
	// 활성화 여부 (활성화, 비활성화, 차단, 탈퇴)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.ACTIVE; // 기본값 ACTIVE
	
	// 차단 기간 관련 설정
	// 차단 시작일
	private LocalDateTime banStartDate;
	
	// 차단 종료일
	private LocalDateTime banEndDate;
	
	// 차단된 기간 (옵션: 수동 차단 기간 설정 시 사용)
	private Integer banDurationDays; // 차단 기간 (일수 기준)
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Column(columnDefinition = "TEXT", length=300)
	private String aboutMe;
	}
