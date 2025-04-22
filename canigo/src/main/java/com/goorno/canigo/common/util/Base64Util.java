package com.goorno.canigo.common.util;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

public class Base64Util {
	
	/*
	 * MultipartFile 리스트를 base64 인코딩된 문자열 리스트로 변환
	 * "data:[MIME];base64,..." 형식
	 */
	public static List<String> encodeFilesToBase64(List<MultipartFile> files) throws IOException {
		if (files == null || files.isEmpty()) return List.of();
		
		return files.stream()
				.filter(file -> file != null && !file.isEmpty())
				.map(file -> {
					try {
						String base64 = Base64.getEncoder().encodeToString(file.getBytes());
						String contentType = file.getContentType();
						return "data:" + contentType + ";base64," + base64;
					} catch (IOException e) {
						throw new RuntimeException("파일 인코딩 중 오류 발생", e);
					}
				})
				.collect(Collectors.toList());
	}
	
	// 단일 MultipartFile을 Base64 문자열로 변환
	public static String encodeFileToBase64(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) return null;
		
		String base64 = Base64.getEncoder().encodeToString(file.getBytes());
		String contentType = file.getContentType();
		return "data:" + contentType + ";base64," + base64;
	}
}
