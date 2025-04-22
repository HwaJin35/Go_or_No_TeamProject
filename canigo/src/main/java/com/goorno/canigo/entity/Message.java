package com.goorno.canigo.entity;

import java.time.LocalDateTime;

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
public class Message extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 메시지를 보낸 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;
	
	// 메시지를 받은 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;
	
	// 메시지 내용
	private String content;
	
	// 메시지 상태(읽음/읽지 않음)
	private boolean isRead = false;
	
	// 메시지 발송 시간
	// BaseEntity의 createdAt과는 다른 개념의 시간
	private LocalDateTime sentTime;
	
	// 메시지 유형 ex) 텍스트, 이미지 등
	private String messageType;
	
	// 메시지가 발송된 후 알림을 생성할 때 사용
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType = NotificationType.MESSAGE;
	
	// 메시지와 관련된 알림을 생성할 수 있는 메서드
	public void createNotification() {
		// 알림 생성 로직 (Notification 엔티티 생성)
		Notification notification = Notification.builder()
				.receiver(this.receiver)
				.type(this.notificationType)
				.content("새 메시지가 도착했습니다.")
				.targetId(this.id)
				.targetType(NotificationTarget.MESSAGE)
				.build();
	}
	
}
