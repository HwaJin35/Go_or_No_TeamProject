package com.goorno.canigo.entity;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.enums.NotificationTarget;
import com.goorno.canigo.entity.enums.NotificationType;

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
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 알림 받는 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;
	
	// 알림 타입 (해시태그, 멘션, 메시지 등)
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	
	// 알림 메시지 ex) "새 댓글 달림", "새 메시지 도착"
	private String content;
	
	// 링크 대상 ID ex) 댓글 ID, 메시지 ID 등
	private Long targetId;
	
	// 알림이 속한 대상 엔티티
	@Enumerated(EnumType.STRING)
	private NotificationTarget targetType;
	
	private boolean isRead = false;
}
