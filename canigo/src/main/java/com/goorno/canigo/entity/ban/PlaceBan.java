package com.goorno.canigo.entity.ban;

import com.goorno.canigo.entity.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "place_ban", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "place_id"})
})
public class PlaceBan extends BaseBan {

    // 차단된 장소
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}