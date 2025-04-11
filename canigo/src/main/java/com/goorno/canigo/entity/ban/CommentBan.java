package com.goorno.canigo.entity.ban;

import com.goorno.canigo.entity.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment_ban", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
public class CommentBan extends BaseBan {

    // 차단된 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;
}