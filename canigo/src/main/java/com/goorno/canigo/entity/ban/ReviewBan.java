package com.goorno.canigo.entity.ban;

import com.goorno.canigo.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "review_ban", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "review_id"})
})
public class ReviewBan extends BaseBan {

    // 차단된 리뷰
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}