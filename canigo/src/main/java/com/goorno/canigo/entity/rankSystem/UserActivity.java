package com.goorno.canigo.entity.rankSystem;

import java.time.LocalDateTime;

import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.ActivityType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class UserActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	// 활동 유형 (예: 댓글 작성, 리뷰 작성 등)
	@Enumerated(EnumType.STRING)
	private ActivityType activityType;
	
	// 관련된 리뷰, 장소, 댓글 등의 ID
	private Long targetId;
	
	// 활동에 의한 점수
	private Integer score;
	
	// 활동 날짜
	private LocalDateTime activityDate;
	
}
