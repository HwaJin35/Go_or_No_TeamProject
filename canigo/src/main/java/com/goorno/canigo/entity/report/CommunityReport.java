package com.goorno.canigo.entity.report;

import com.goorno.canigo.entity.Community;

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
		name = "community_report"
)
public class CommunityReport extends BaseReport {

	// 신고 대상 커뮤니티 게시글
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;
}
