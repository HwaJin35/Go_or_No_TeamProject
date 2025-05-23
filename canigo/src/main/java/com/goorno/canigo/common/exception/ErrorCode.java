package com.goorno.canigo.common.exception;

public enum ErrorCode {
	
	// 400 Bad Request
	PASSWORD_MISMATCH(400, "현재 비밀번호가 일치하지 않습니다."),
	PASSWORD_CONFIRM_MISMATCH(400, "새 비밀번호가 일치하지 않습니다."),
	PASSWORD_DUPLICATE(400, "새 비밀번호는 현재 비밀번호와 달라야 합니다."),
	PASSWORD_TOO_SHORT(400, "비밀번호는 최소 8자 이상이어야 합니다."),
	NICKNAME_DUPLICATE(400, "이미 사용 중인 닉네임입니다."),
	EMAIL_NOT_VERIFIED(400, "이메일 인증이 완료되지 않았습니다."),
	ALREADY_REGISTERED(400, "이미 가입이 완료된 사용자입니다."),
	EMAIL_NOT_FOUND(400, "이메일 인증이 먼저 필요합니다."),
	
	//401 Unauthorized
	AUTHENTICATION_FAILED(401, "인증에 실패했습니다."),
	AUTHENTICATION_USER_NOT_FOUND(401, "존재하지 않는 사용자입니다."),
	BAD_CREDENTIALS(401, "비밀번호가 일치하지 않습니다."),
	ACCOUNT_DISABLED(401, "비활성화된 계정입니다."),
	
	//403 Forbidden
	UNAUTHORIZED_USER(403, "접근 권한이 없습니다."),
	
	//404 Not Found
	USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다.");
	
	private final int status;
	private final String message;
	
	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
}
