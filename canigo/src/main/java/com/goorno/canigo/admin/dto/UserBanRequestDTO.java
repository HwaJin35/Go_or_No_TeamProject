package com.goorno.canigo.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자의 유저 차단 요청 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBanRequestDTO {
	
	private Long userId;
	private LocalDateTime banStartDate;
	private LocalDateTime banEndDate;
	private Integer banDurationDays;
}