package com.goorno.canigo.entity.enums;

public enum CategoryType {
	FOOD("음식"),
	DRINK("음료"),
	SPOT("명소"),
	LANDMARK("상징물"),
	STORE("가게"),
	NATURE("자연공간"),
	ETC("기타");

	private final String categoryName;
	
	CategoryType(String categoryName) {
		this.categoryName = categoryName;
	}
	
	// 카테고리의 설명을 반환
	public String getCategoryName() {
		return categoryName;
	}
	
	// 한글을 enum 상수로 변환하는 커스텀 메서드 
	public static CategoryType fromDescription(String categoryName) {
		for (CategoryType category : CategoryType.values()) {
			if (category.getCategoryName().equals(categoryName)) {
				return category;
			}
		}
		throw new IllegalArgumentException("Unknown categoryName: " + categoryName);
	}
	
}
