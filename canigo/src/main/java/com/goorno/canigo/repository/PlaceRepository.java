package com.goorno.canigo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Place;

// JPA 기반 인터페이스(CRUD)
// 데이터베이스 작업을 대신 처리해줌
public interface PlaceRepository extends JpaRepository<Place, Long> {

}
