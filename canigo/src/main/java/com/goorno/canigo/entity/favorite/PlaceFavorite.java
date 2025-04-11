package com.goorno.canigo.entity.favorite;

import com.goorno.canigo.entity.Place;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "place_favorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "place_id"})
})
public class PlaceFavorite extends BaseFavorite {

    // 즐겨찾기 대상 장소
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
