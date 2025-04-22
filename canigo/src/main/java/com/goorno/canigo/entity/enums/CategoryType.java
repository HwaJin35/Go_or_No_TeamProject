package com.goorno.canigo.entity.enums;

public enum CategoryType {
	FOOD("음식"),
	DRINK("음료"),
	SPOT("명소"),
	LANDMARK("상징물"),
	STORE("가게"),
	NATURE("자연공간"),
	ETC("기타");

	private final String description;
	
	CategoryType(String description) {
		this.description = description;
	}
	
	// 카테고리의 설명을 반환
	public String getDescription() {
		return description;
	}
	
}
