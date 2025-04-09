package com.goorno.canigo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

// ### CanigoApplication.java에서 @EnableJpaAuditing 필수!

@MappedSuperclass // 해당 클래스 상속 시 필드 그대로 가져가게 해줌
@EntityListeners(AuditingEntityListener.class) // 변경 감지 가능케 해줌
@Getter
public abstract class BaseEntity {

	@CreatedBy
	@Column(updatable = false)
	private String createdBy;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@LastModifiedBy
	private String updatedBy;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
}
