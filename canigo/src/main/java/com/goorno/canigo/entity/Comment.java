package com.goorno.canigo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.ban.CommentBan;
import com.goorno.canigo.entity.enums.CommentTargetType;
import com.goorno.canigo.entity.like.CommentLikeDislike;
import com.goorno.canigo.entity.report.CommentReport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
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
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 댓글 내용
	@Lob
	private String content;
	
	// 댓글을 단 사용자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	// 댓글을 단 대상
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CommentTargetType targetType;

	// 서비스 레벨에서 수동으로 처리할 타겟 아이디
	@Column(nullable = false)
	private Long targetId;
	
	// 대댓글 구현을 위한 자기참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
	// 커뮤니티
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	private Community community;
	
	// 리뷰
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;
	
	// 이 댓글의 대댓글 조회
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore // 순환 참조 문제 방지(?)
	private List<Comment> children = new ArrayList<>();
	
	// 좋아요/싫어요
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentLikeDislike> likeDislikes = new ArrayList<>();
	
	// 신고
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentReport> reports = new ArrayList<>();

	// 차단
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentBan> bans = new ArrayList<>();
	
	// 해시태그
	@ManyToMany
	@JoinTable(
		name = "comment_hashtag",
		joinColumns = @JoinColumn(name = "comment_id"),
		inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	private List<Hashtag> hashtags = new ArrayList<>();
	
	// 멘션 
	@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
	private List<Mention> mentions = new ArrayList<>();
}
