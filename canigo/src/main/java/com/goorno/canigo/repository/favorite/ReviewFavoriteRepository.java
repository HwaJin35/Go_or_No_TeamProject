package com.goorno.canigo.repository.favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.favorite.ReviewFavorite;

public interface ReviewFavoriteRepository extends JpaRepository<ReviewFavorite, Long> {

}
