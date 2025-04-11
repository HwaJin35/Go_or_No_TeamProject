package com.goorno.canigo.entity.like;

import com.goorno.canigo.entity.Review;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "review_like_dislike",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "review_id"})
)
public class ReviewLikeDislike extends BaseLikeDislike {

	// 좋아요 or 싫어요 대상 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}