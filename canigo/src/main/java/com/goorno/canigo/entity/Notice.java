package com.goorno.canigo.entity;

import java.util.List;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.enums.NoticeType;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
public class Notice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 제목
	private String title;
	
	// 내용
	@Lob
	private String content;
	
	// 작성자 (관리자만 가능)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private User writer;
	
	// 고정 여부
	private boolean pinned;
	
	// 고정 공지일 경우의 정렬 순서 (낮을수록 상단)
	private Integer pinOrder;
	
	// 공지 분류
	@Enumerated(EnumType.STRING)
	private NoticeType noticeType;
	
	// 첨부된 파일 경로들
	@ElementCollection
	@Lob
	private List<String> uploadFiles;
}
