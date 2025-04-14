package com.goorno.canigo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.ban.CommentBan;
import com.goorno.canigo.entity.like.CommentLikeDislike;
import com.goorno.canigo.entity.report.CommentReport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	
	// 댓글이 달린 리뷰
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;
	
	// 대댓글 구현을 위한 자기참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
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
}
