package com.goorno.canigo.entity;

import java.util.ArrayList;
import java.util.List;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.ban.PlaceBan;
import com.goorno.canigo.entity.enums.CategoryType;
import com.goorno.canigo.entity.favorite.PlaceFavorite;
import com.goorno.canigo.entity.like.PlaceLikeDislike;
import com.goorno.canigo.entity.report.PlaceReport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 실제 저장 대상 엔티티 클래스
// 장소 데이터를 담고 처리하는 핵심 도메인 객체
@Entity
@Getter
@Setter
@NoArgsConstructor   // 기본 생성자 (파라미터 없는 생성자)를 자동 생성
@AllArgsConstructor	 // 모든 필드를 파라미터로 받는 생성자를 자동 생성
@Builder			 // 빌더 패턴을 자동 생성
@Table(name="place", uniqueConstraints= {
		@UniqueConstraint(columnNames = {"latitude", "longitude"})
})	// 위도, 경도 유니크화
public class Place extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 장소명
	private String name;
	
	// 장소 설명
	@Column(length = 1000)
	private String description;
	
	// 장소 위도/경도
	private Double latitude;
	private Double longitude;
	
	// 장소 이미지(다중)
	@ElementCollection
	@CollectionTable(
		name = "place_files",
		joinColumns = @JoinColumn(name = "place_id")
	)
	@Column(name = "upload_file")
	@Lob
	private List<String> uploadFiles;
	
	// 장소 카테고리
	@Enumerated(EnumType.STRING) // ENUM 값이 DB에 문자열로 저장되도록 지정
	private CategoryType category;
	
	// 장소 등록자 정보
	// 내 클래스가 Many, 상대방이 One
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩, place.getUser() 직접 호출 전까지 User 객체 가져오지 않음
	@JoinColumn(name = "user_id") // DB에 생성될 외래 키 컬럼 이름 정함
	private User user;
	
	// 리뷰 목록
	// mappedBy="place": 양방향 연관관계에서 연관관계의 주인이 아님을 명시
	//					 Review 쪽의 place 필드가 담당함
	// cascade = CascadeType.ALL: Place에 대한 연산(수정 등)이 Review에도 전파되도록 함
	// orphanRemoval = true: 고아 객체 제거 옵션(리뷰 제거 시 DB에서도 삭제)
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();
	
	// 좋아요/싫어요
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceLikeDislike> likeDislikes = new ArrayList<>();
	
	// 신고
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceReport> reports = new ArrayList<>();
	
	// 차단
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceBan> bans = new ArrayList<>();
	
	// 즐겨찾기
	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlaceFavorite> favorites = new ArrayList<>();
	
	// 해시태그
	@ManyToMany
	@JoinTable(
		name = "place_hashtag",
		joinColumns = @JoinColumn(name = "place_id"), // 현재 엔티티의 외래키
		inverseJoinColumns = @JoinColumn(name = "hashtag_id") // 반대편 엔티티의 외래키
	)
	private List<Hashtag> hashtags = new ArrayList<>();
}
