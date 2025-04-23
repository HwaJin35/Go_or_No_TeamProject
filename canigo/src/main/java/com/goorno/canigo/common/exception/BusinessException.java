package com.goorno.canigo.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private int statusCode; // 상태 코드
	private String errorMessage; // 오류 메시지
	
	// 기본 생성자
	public BusinessException(String message) {
		super(message);
		this.errorMessage = message;
		this.statusCode = 400; // 기본 400번 BadRequest
	}
	
	// 상태 코드와 메시지를 포함한 생성자
	public BusinessException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
		this.errorMessage = message;
	}
}
