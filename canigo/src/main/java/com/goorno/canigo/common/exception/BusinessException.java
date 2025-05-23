package com.goorno.canigo.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;
	
	// 기본 생성자
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	
	public int getStatusCode() {
        return errorCode.getStatus();
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }
}