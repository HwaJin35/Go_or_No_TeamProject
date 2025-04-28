package com.goorno.canigo.service.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goorno.canigo.common.util.FileValidationUtil;
import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.entity.User;
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
	@Transactional
	@Override
	public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
		
		 // 닉네임 중복 검사
		 if (userRepository.existsByNickname(userRequestDTO.getNickname())) {
			 throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
		 }
		 // 이메일 중복 검사
		 if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
			 throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		 }
		 // 프로필 이미지 파일 검증
		 if (userRequestDTO.getFiles() != null && !userRequestDTO.getFiles().isEmpty()) {
		     FileValidationUtil.validateFiles(userRequestDTO.getFiles());
		 }
		 
		 // 비밀번호 암호화
		 String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
		 userRequestDTO.setPassword(encodedPassword);
		 
		 User user = userMapper.toEntity(userRequestDTO);
		 // ACTIVE, ROLE_USER 상태로 설정
		 user.setStatus(Status.ACTIVE);
		 user.setRole(Role.ROLE_USER);
		 User savedUser = userRepository.save(user);
				 
		 return userMapper.toResponseDTO(savedUser);
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
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		return userMapper.toResponseDTO(user);
	}

	// 회원정보 수정 메서드
	@Transactional
    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO updateDTO) {
		 User user = userRepository.findById(id)
		        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		userMapper.updateUserFromDTO(user, updateDTO);
		User updatedUser = userRepository.save(user);

		return userMapper.toResponseDTO(updatedUser);
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

	// 공통 메서드
	// ID로 사용자 조회
	private User getUserOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
	}
}