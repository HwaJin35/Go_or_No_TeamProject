package com.goorno.canigo.entity.report;

import com.goorno.canigo.entity.Place;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
		name = "place_report"
)
public class PlaceReport extends BaseReport {

	// 신고 대상 장소
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;
}
