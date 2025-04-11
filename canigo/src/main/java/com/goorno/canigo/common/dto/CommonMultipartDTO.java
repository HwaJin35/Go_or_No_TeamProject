package com.goorno.canigo.common.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonMultipartDTO {
	private MultipartFile image;
	// 업로드할 이미지 파일 담는 필드
	// 스프링이 자동으로 처리 해준다.
}

// 공통적으로 사용할 파일 업로드용 필드를 정의한 상속용 부모 클래스
// 앞으로 이미지가 필요한 여러 DTO 클래스에서 상속
