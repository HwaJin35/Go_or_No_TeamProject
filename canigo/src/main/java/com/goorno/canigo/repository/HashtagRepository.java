package com.goorno.canigo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long>{
	Optional<Hashtag> findByName(String name);
}
