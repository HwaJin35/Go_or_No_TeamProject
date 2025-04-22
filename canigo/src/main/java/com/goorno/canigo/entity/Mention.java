package com.goorno.canigo.entity;

import com.goorno.canigo.common.entity.BaseEntity;

import jakarta.persistence.Entity;
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
public class Mention extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 멘션한 사람
	@ManyToOne
	@JoinColumn(name = "sender_id")
	private User sender;
	
	// 멘션된 사람
	@ManyToOne
	@JoinColumn(name = "target_id", nullable = false)
	private User target;
	
	// 멘션이 달린 리뷰
	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;
	
	// 멘션이 달린 댓글
	@ManyToOne
	@JoinColumn(name = "comment_id")
	private Comment comment;
}
