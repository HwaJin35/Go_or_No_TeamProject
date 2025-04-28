package com.goorno.canigo.common.util;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
/**
 * FileValidationUtil
 * - 업로드된 파일의 유효성을 검사하는 유틸 클래스
 * - 이미지 파일 여부 검사, 파일 크기 제한 검사 제공
 * 
 * 사용 대상
 * - User, Place 등 파일 업로드가 필요한 모든 엔터티
 */
public class FileValidationUtil {

    // 최대 허용 용량 (50MB)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;	// 50MB

    // 단일 파일이 이미지 파일인지 검사
    // 이미지 파일이면 true, 아니면 false
    public static boolean isImage(MultipartFile file) {
    	if(file == null || file.isEmpty()) {
    		return false;
    	}
    	String contentType = file.getContentType();
    	return contentType != null && contentType.startsWith("image/");
    }
    
    // 단일 파일의 크기가 허용 범위를 초과하는지 검사
    public static void validateFileSize(MultipartFile file) {
    	if (file != null && file.getSize() > MAX_FILE_SIZE ) {
    		throw new IllegalArgumentException("업로드 파일 크기가 50MB를 초과했습니다.");
    	}
    }
    
    // 단일 파일에 대해 이미지 여부 + 크기 검사를 동시에 수행
    public static void validateSingleFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // 파일이 없는 경우는 통과 (선택적 업로드 허용)
        }
        if (!isImage(file)) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
        validateFileSize(file);
    }
    
    // 다수의 파일이 대해 이미지 여부 + 크기 검사를 동시에 수행
    public static void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return; // 파일이 없는 경우는 통과
        }
        for (MultipartFile file : files) {
            validateSingleFile(file);
        }
    }
}