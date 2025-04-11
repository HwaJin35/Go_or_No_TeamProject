package com.goorno.canigo.entity.like;

import com.goorno.canigo.common.entity.BaseEntity;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.LikeDislikeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseLikeDislike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 좋아요 or 싫어요
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikeDislikeType type;
}