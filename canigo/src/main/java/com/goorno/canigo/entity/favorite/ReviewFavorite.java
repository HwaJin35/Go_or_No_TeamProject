package com.goorno.canigo.entity.favorite;

import com.goorno.canigo.entity.Review;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "review_favorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "review_id"})
})
public class ReviewFavorite extends BaseFavorite {

    // 즐겨찾기 대상 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}
