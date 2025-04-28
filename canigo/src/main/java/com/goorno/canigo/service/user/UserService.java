package com.goorno.canigo.service.user;

import java.util.List;

import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;

public interface UserService {
	
	// 회원가입
	UserResponseDTO createUser(UserRequestDTO userRequestDTO);
	
	// 전체 회원 조회
	List<UserResponseDTO> getAllUsers();
	
	// 이메일로 단일 조회
	UserResponseDTO getUserByEmail(String email);
	
	// 닉네임으로 단일 조회
	UserResponseDTO getUserByNickname(String nickname);
	
	// 나의 정보 조회
	UserResponseDTO findMyDataFromEmail(String email);
	
	// 회원 정보 수정
	UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updatetDTO);
	
	// 회원 비활성화
	void deactivateUser(Long id);
	
	// 회원 탈퇴
	void deleteUser(Long id);
}
