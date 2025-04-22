package com.goorno.canigo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Hashtag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// ex) "#음식", "#음료" 등
	private String name;
	
	// 해시태그가 달린 장소
	@ManyToMany(mappedBy = "hashtags")
	private List<Place> places = new ArrayList<>();
	
	// 해시태그가 달린 리뷰
	@ManyToMany(mappedBy = "hashtags")
	private List<Review> reviews = new ArrayList<>();
	
	// 해시태그가 달린 댓글
	@ManyToMany(mappedBy = "hashtags")
	private List<Comment> comments = new ArrayList<>();
	
	// 해시태그는 재사용이 가능하며, 다대다 관계
}
