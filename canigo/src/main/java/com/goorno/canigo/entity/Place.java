package com.goorno.canigo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 실제 저장 대상 엔티티 클래스
// 장소 데이터를 담고 처리하는 핵심 도메인 객체
@Entity
@Getter
@Setter
public class Place extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private Double latitude;
	private Double longitude;
	private String imageUrl;
}
