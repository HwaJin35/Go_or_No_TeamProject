package com.goorno.canigo.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.dto.user.PasswordChangeDTO;
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
	
	// 이메일 중복 확인 API
	@GetMapping("/check-email")
	public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam("email") String email) {
		boolean exists = userService.checkEmailDuplicate(email);
		Map<String, Boolean> response = new HashMap<>();
		response.put("exists", exists);
		return ResponseEntity.ok(response);
		// Json 응답 형태: { "exists" : true(false) }
	}
	
	// 닉네임 중복 확인 API
	@GetMapping("/check-nickname")
	public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
	    boolean exists = userService.checkNicknameDuplicate(nickname);
	    Map<String, Boolean> response = new HashMap<>();
	    response.put("exists", exists);
	    return ResponseEntity.ok(response);
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
	 @PutMapping("/me")
	 public ResponseEntity<UserResponseDTO> updateUser(
	            @AuthenticationPrincipal UserDetails userDetails,
	            @Valid @ModelAttribute UserUpdateRequestDTO updateDTO) {
		 String email = userDetails.getUsername();
		 Long id = userService.getUserByEmail(email).getId();
	     UserResponseDTO updatedUser = userService.updateUser(id, updateDTO);
	     return ResponseEntity.ok(updatedUser);
	 }
	 
	 // 비밀번호 수정 API
	 @PutMapping("/me/password")
	 public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody PasswordChangeDTO dto){
		String email = userDetails.getUsername();   // 로그인한 사용자의 이메일 
		userService.changePassword(email, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	 }
	
	// 회원 비활성화 API
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<Void> deactiveUser(@PathVariable("id") Long id) {
		userService.deactivateUser(id);
		return ResponseEntity.noContent().build();
	}
	
	// 회원 탈퇴 API
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
		String email = userDetails.getUsername();
		Long id = userService.getUserByEmail(email).getId();
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}