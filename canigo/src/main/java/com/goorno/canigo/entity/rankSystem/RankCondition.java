package com.goorno.canigo.entity.rankSystem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class RankCondition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 등급 이름 (새싹, 브론즈, 실버, 골드, 플래티넘, 다이아, 챌린저)
	private String grade;
	
	// 해당 등급에 도달하기 위한 최소 점수
	private Integer minimumScore;
	
	// 등급의 정렬 순서 (낮을수록 상단)
	private Integer order;
}
