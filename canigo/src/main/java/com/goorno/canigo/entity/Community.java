package com.goorno.canigo.entity;

import java.util.List;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.enums.CommunityType;
import com.goorno.canigo.entity.like.CommunityLikeDislike;
import com.goorno.canigo.entity.report.CommunityReport;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
public class Community extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 제목
	private String title;
	
	// 내용
	@Lob
	private String content;
	
	// 작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private User writer;
	
	// 게시물 카테고리 (공지, 자유, 질문)
	@Enumerated(EnumType.STRING)
	private CommunityType communityType;
	
	// 고정 여부
	private boolean pinned;
	
	// 고정 공지일 경우의 정렬 순서 (낮을수록 상단)
	private Integer pinOrder;
	
	// 조회수
	private Integer views = 0; // 기본값 0, 조회수 증가시 변경
	
	// 댓글
	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;
	
	// 좋아요
	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityLikeDislike> likeDislikes;
	
	// 신고 
	@OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunityReport> reports;
	
	// 첨부된 파일 경로들
	@ElementCollection
	@Lob
	private List<String> uploadFiles;
}
