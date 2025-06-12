package com.goorno.canigo.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private int status;
	private String error;
	private String message;
	private String path;
}
