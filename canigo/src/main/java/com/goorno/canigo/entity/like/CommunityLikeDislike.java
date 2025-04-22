package com.goorno.canigo.entity.like;

import com.goorno.canigo.entity.Community;

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
    name = "community_like_dislike",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "community_id"})
)
public class CommunityLikeDislike extends BaseLikeDislike {

	// 좋아요 or 싫어요 대상 커뮤니티 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}