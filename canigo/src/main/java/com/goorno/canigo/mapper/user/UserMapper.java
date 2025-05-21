package com.goorno.canigo.mapper.user;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;

/**
 * UserRequestDTO <-> User Entity 변환을 담당하는 매퍼 클래스 - Service 계층에서 호출하여 사용
 */

@Component // 스프링 빈으로 등록하여 Service에 주입하여 사용
public class UserMapper {
	// UserRequestDTO → User Entity로 변환
	public User toEntity(UserRequestDTO dto) {
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setNickname(dto.getNickname());
		user.setAuthProvider(dto.getAuthProvider());
		user.setRole(Role.ROLE_USER);

		// 파일이 있을 경우 첫 번째 파일을 프로필 이미지로 설정
		if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
			MultipartFile file = dto.getUploadFiles().get(0); // 단일 파일처리
			try {
				String base64 = Base64Util.encodeFileToBase64(file);
				user.setProfileImageBase64(base64);
			} catch (IOException e) {
				throw new RuntimeException("프로필 이미지 인코딩 실패.", e);
			}
		}
		return user;
	}
	
	//User Entity → UserResponseDTO로 변환
	public UserResponseDTO toResponseDTO(User user) {
		return UserResponseDTO.builder()
				.id(user.getId())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.profileImageFile(user.getProfileImageBase64())
				.authProvider(user.getAuthProvider())
				.role(user.getRole())
				.status(user.getStatus())
				.banStartDate(user.getBanStartDate())
				.banEndDate(user.getBanEndDate())
				.banDurationDays(user.getBanDurationDays())
				.aboutMe(user.getAboutMe())
				.build();
	}

	// 회원 정보 수정 시 User Entity에 요청 DTO 적용 - 닉네임, 프로필 이미지, 소개글만 수정
	public void updateUserFromDTO(User user, UserUpdateRequestDTO dto) {
		user.setNickname(dto.getNickname());
		if (dto.getAboutMe() != null) {
			user.setAboutMe(dto.getAboutMe());
		}
		// 새로운 프로필 이미지가 있을 경우만 업데이트
		if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
			MultipartFile file = dto.getUploadFiles().get(0);
			try {
				String base64 = Base64Util.encodeFileToBase64(file);
				user.setProfileImageBase64(base64);
			} catch (IOException e) {
				throw new RuntimeException("프로필 이미지 수정 실패", e);
			}
		}
	}
}