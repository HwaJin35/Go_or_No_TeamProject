package com.goorno.canigo.dto.user;

import com.goorno.canigo.common.dto.CommonMultipartDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 유저 정보 수정용 요청 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDTO extends CommonMultipartDTO {

	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@Size(max = 300, message = "소개글은 300자 이내여야 합니다.")
	private String aboutMe;

}