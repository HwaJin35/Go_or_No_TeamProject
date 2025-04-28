package com.goorno.canigo.common.exception.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.goorno.canigo.common.exception.BusinessException;
import com.goorno.canigo.common.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// 커스텀 BusinessException 처리
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handlerBusinessException(BusinessException ex, HttpServletRequest request) {
		log.warn("BusinessException: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.valueOf(ex.getStatusCode()), ex.getErrorMessage(), request.getRequestURI());
	}

	// IllegalArgumentException
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
		log.warn("IllegalArgumentException: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
	}
	
	// NullPointerException
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullpointer(NullPointerException ex, HttpServletRequest request) {
		log.error("NullPointerException: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", request.getRequestURI());
	}
	
	// MethodArgumentNotValidException (유효성 검증 실패)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(field -> field.getField() + ": " + field.getDefaultMessage())
				.findFirst()
				.orElse("유효성 검증 실패");
		log.warn("Validation Error: {}", errorMessage);
		return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request.getRequestURI());
	}
	
	// BadCredentialsException (비밀번호 틀림) 처리
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		log.warn("BadCredentialsException: {}", ex.getMessage());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
	}
	
	// 그 외 Exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
		log.error("Unhandled Exception: ", ex);
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.", request.getRequestURI());
	}
	
	// 공통 응답 생성 메서드
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
		ErrorResponse response = new ErrorResponse(
				status.value(),
				status.getReasonPhrase(),
				message,
				path
		);
		return new ResponseEntity<>(response, status);
	}
}
