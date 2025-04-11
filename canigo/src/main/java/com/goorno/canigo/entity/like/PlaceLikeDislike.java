package com.goorno.canigo.entity.like;

import com.goorno.canigo.entity.Place;

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
    name = "place_like_dislike",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "place_id"})
)
public class PlaceLikeDislike extends BaseLikeDislike {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}