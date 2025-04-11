package com.goorno.canigo.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.like.CommentLikeDislike;

public interface CommentLikeDislikeRepository extends JpaRepository<CommentLikeDislike, Long> {

}
