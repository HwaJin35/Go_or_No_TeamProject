package com.goorno.canigo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonMultipartDTO {
	private MultipartFile image;
}
