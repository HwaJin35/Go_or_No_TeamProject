package com.goorno.canigo.common.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
	
	private final ErrorCode errorCode;
	
	public UserException(ErrorCode errorCode) {
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
