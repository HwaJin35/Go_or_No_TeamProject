package com.goorno.canigo.entity;

import java.util.ArrayList;
import java.util.List;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.ban.ReviewBan;
import com.goorno.canigo.entity.favorite.ReviewFavorite;
import com.goorno.canigo.entity.like.ReviewLikeDislike;
import com.goorno.canigo.entity.report.ReviewReport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 리뷰 제목
	private String title;
	
	// 리뷰 (상세보기) 내용
	@Lob
	private String content;
	
	// 리뷰 (상세보기) 이미지
	@ElementCollection
	@CollectionTable(
	    name = "review_files",
	    joinColumns = @JoinColumn(name = "review_id") // 연결 키
	)
	@Column(name = "upload_file", columnDefinition = "LONGTEXT")
	@Lob
	private List<String> uploadFiles;
	
	// 리뷰 작성자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	// 장소 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;
	
	// 댓글
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();
	
	// 좋아요/싫어요
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewLikeDislike> likeDislikes = new ArrayList<>();
	
	// 신고
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewReport> reports = new ArrayList<>();
	
	// 차단
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewBan> bans = new ArrayList<>();
	
	// 즐겨찾기
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewFavorite> favorites = new ArrayList<>();
	
	// 해시태그
	@ManyToMany
	@JoinTable(
		name = "review_hashtag",
		joinColumns = @JoinColumn(name = "review_id"),
		inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	private List<Hashtag> hashtags = new ArrayList<>();
	
	// 멘션
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
	private List<Mention> mentions = new ArrayList<>();
	
}
