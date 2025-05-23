package com.goorno.canigo.service.user;

import java.io.IOException;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.goorno.canigo.common.exception.ErrorCode;
import com.goorno.canigo.common.exception.UserException;
import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.common.util.FileValidationUtil;
import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.AuthProviderType;
import com.goorno.canigo.entity.enums.Role;
import com.goorno.canigo.entity.enums.Status;
import com.goorno.canigo.mapper.user.UserMapper;
import com.goorno.canigo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserServiceImpl 
 * - User CRUD 및 비즈니스 로직을 처리하는 클래스 
 * - 파일 업로드(FileEntity) 연동도 포함됨
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	// 회원가입 메서드
	// 이메일 인증을 완료한 PENDING 유저를 ACITVE로 확정
	@Transactional
	@Override
	public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

		// 이메일 기반으로 임시 유저(PENDING) 조회
		User user = userRepository.findByEmail(userRequestDTO.getEmail())
				.orElseThrow(() -> new UserException(ErrorCode.EMAIL_NOT_FOUND)
						);

		// 인증 여부 확인
		if (user.isVerified() == false) {
			throw new UserException(ErrorCode.EMAIL_NOT_VERIFIED);
		}

		// 이미 가입된 유저라면 가입 차단
		if (user.getStatus() == Status.ACTIVE) {
			throw new UserException(ErrorCode.ALREADY_REGISTERED);
		}

		// 닉네임 중복 검사
		if (userRepository.existsByNickname(userRequestDTO.getNickname())) {
			throw new UserException(ErrorCode.NICKNAME_DUPLICATE);
		}

		// 프로필 이미지 파일 검증
		if (userRequestDTO.getUploadFiles() != null && !userRequestDTO.getUploadFiles().isEmpty()) {
			FileValidationUtil.validateFiles(userRequestDTO.getUploadFiles());
		}
		// 비밀번호 유효성 검사
		if (userRequestDTO.getPassword() == null || userRequestDTO.getPassword().length() < 8) {
			throw new UserException(ErrorCode.PASSWORD_TOO_SHORT);
		}

		// 비밀번호 암호화 후 설정
		String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
		user.setPassword(encodedPassword);

		// 인증을 마친 유저를 확정 정보로 덮어씀(확정 가입 처리 - ACTIVE)
		user.setNickname(userRequestDTO.getNickname());
		user.setAuthProvider(AuthProviderType.LOCAL);
		user.setRole(Role.USER);
		user.setStatus(Status.ACTIVE);

		if (userRequestDTO.getUploadFiles() != null && !userRequestDTO.getUploadFiles().isEmpty()) {
			MultipartFile file = userRequestDTO.getUploadFiles().get(0);
			try {
				String base64 = Base64Util.encodeFileToBase64(file);
				user.setProfileImageBase64(base64);
			} catch (IOException e) {
				throw new RuntimeException("프로필 이미지 처리 실패", e);
			}
		}
		// 저장 및 반환
		User savedUser = userRepository.save(user);
		return userMapper.toResponseDTO(savedUser);
	}

	// 이메일 중복 확인 메서드
	// 인증을 받았고, 상태가 ACTIVE인 유저 email이 이미 존재하는지 확인
	@Override
	public boolean checkEmailDuplicate(String email) {
		return userRepository.findByEmail(email)
				.filter(user -> user.isVerified() && user.getStatus() == Status.ACTIVE)
				.isPresent();
	}

	// 닉네임 중복 확인 메서드
	@Override
	public boolean checkNicknameDuplicate(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	// 전체 회원 조회 메서드
	@Override
	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll()
				.stream()
				.map(userMapper::toResponseDTO)
				.toList();
	}

	// 이메일로 단일 회원 조회 메서드
	@Override
	public UserResponseDTO getUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("이메일로 사용자를 찾을 수 없습니다."));
		return userMapper.toResponseDTO(user);
	}

	// 닉네임으로 단일 회원 조회 메서드
	@Override
	public UserResponseDTO getUserByNickname(String nickname) {
		User user = userRepository.findByNickname(nickname)
				.orElseThrow(() -> new IllegalArgumentException("닉네임으로 사용자를 찾을 수 없습니다."));
		return userMapper.toResponseDTO(user);
	}

	// 나의 정보 조회 메서드
	@Override
	public UserResponseDTO findMyDataFromEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		return userMapper.toResponseDTO(user);
	}

	// 회원정보 수정 메서드
	@Transactional
	@Override
	public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateDTO) {
		User user = getUserOrThrow(id);
		String newNickname = updateDTO.getNickname();
		// 닉네임이 변경되었고, 다른 사용자가 해당 닉네임을 쓰고 있다면 예외 처리
		if (newNickname != null && !newNickname.equals(user.getNickname())) {
			boolean nicknameExists = userRepository.existsByNickname(newNickname);
			if (nicknameExists) {
				throw new UserException(ErrorCode.NICKNAME_DUPLICATE);
			}
		}
		userMapper.updateUserFromDTO(user, updateDTO);
		User updatedUser = userRepository.save(user);
		return userMapper.toResponseDTO(updatedUser);
	}

	// 비밀번호 수정 메서드
	@Transactional
	@Override
	public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		// 입력한 현재 비밀번호가 실제 DB에 저장된 비밀번호와 일치하는지 검사
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new UserException(ErrorCode.PASSWORD_MISMATCH);
		}

		// 새 비밀번호와 확인 비밀번호 일치 여부 검사
		if (!newPassword.equals(confirmPassword)) {
			throw new UserException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
		}

		// 새 비밀번호와 현재 비밀번호 중복 여부 검사
		if (newPassword.equals(currentPassword)) {
			throw new UserException(ErrorCode.PASSWORD_DUPLICATE);
		}
		// 새 비밀번호 유효성 검사
		if (newPassword == null || newPassword.length() < 8) {
			throw new UserException(ErrorCode.PASSWORD_TOO_SHORT);
		}

		// 새 비밀번호를 인코딩하여 DB에 저장
		String encodedNewPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedNewPassword);
	}

	// 회원 비활성화 메서드
	@Transactional
	@Override
	public void deactivateUser(Long id) {
		User user = getUserOrThrow(id);
		user.setStatus(Status.INACTIVE);
		userRepository.save(user);
	}

	// 회원 탈퇴 메서드
	@Override
	@Transactional
	public void deleteUser(Long id) {
		User user = getUserOrThrow(id);
		userRepository.delete(user);
	}

	// 공통 메서드
	// ID로 사용자 조회
	private User getUserOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserException(
						ErrorCode.USER_NOT_FOUND));
	}
}