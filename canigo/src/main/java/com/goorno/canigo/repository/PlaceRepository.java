package com.goorno.canigo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Place;

// JPA 기반 인터페이스(CRUD)
// 데이터베이스 작업을 대신 처리해줌
public interface PlaceRepository extends JpaRepository<Place, Long> {

	// 위도와 경도로 장소 찾기 (유니크해야 하므로, 하나만 반환)
	Optional<Place> findByLatitudeAndLongitude(Double latitue, Double longitude);
	
	// 장소 목록 가져오기 ex) 카테고리별로 필터링, 정렬 등
	List<Place> findByCategory(String category);
	
	// 장소 ID로 찾기
	Optional<Place> findById(Long id);
}
