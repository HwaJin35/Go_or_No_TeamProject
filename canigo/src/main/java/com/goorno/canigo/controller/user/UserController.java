package com.goorno.canigo.controller.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.goorno.canigo.dto.user.UserRequestDTO;
import com.goorno.canigo.dto.user.UserResponseDTO;
import com.goorno.canigo.dto.user.UserUpdateRequestDTO;
import com.goorno.canigo.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	// 회원가입 API
	@PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @ModelAttribute UserRequestDTO requestDTO) {
        UserResponseDTO createdUser = userService.createUser(requestDTO);
        return ResponseEntity.ok(createdUser);
    }

	// 전체 회원 조회 API
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	// 이메일로 회원 조회 API
	@GetMapping("/email/{email}")
	public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable("email") String email) {
		UserResponseDTO user = userService.getUserByEmail(email);
		return ResponseEntity.ok(user);
	}
	
	// 닉네임으로 회원 조회 API
	@GetMapping("/nickname/{nickname}")
	public ResponseEntity<UserResponseDTO> getUserByNickname(@PathVariable("nickname") String nickname) {
		UserResponseDTO user = userService.getUserByNickname(nickname);
		return ResponseEntity.ok(user);
	}
	
	// 나의 정보 조회
	@GetMapping("/me")
	 public ResponseEntity<UserResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // 로그인한 사용자 이메일로 사용자 조회
        UserResponseDTO user = userService.findMyDataFromEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
        
    }
	
	// 회원 정보 수정 API
	 @PutMapping("/{id}")
	    public ResponseEntity<UserResponseDTO> updateUser(
	            @PathVariable("id") Long id,
	            @Valid @ModelAttribute UserUpdateRequestDTO updateDTO) {
	        UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
	        return ResponseEntity.ok(updatedUser);
	    }
	
	// 회원 비활성화 API
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<Void> deactiveUser(@PathVariable("id") Long id) {
		userService.deactivateUser(id);
		return ResponseEntity.noContent().build();
	}
	
	// 회원 탈퇴 API
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}