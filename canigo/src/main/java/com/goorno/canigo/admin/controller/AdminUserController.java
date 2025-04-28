package com.goorno.canigo.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goorno.canigo.admin.service.AdminUserService;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.Status;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

	private final AdminUserService adminUserService;
	
	@Autowired
	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
	
	// 회원 목록 조회
	@GetMapping
	public List<User> getAllUsers() {		
		return adminUserService.getAllUsers();
	}
	
	// 회원 상태 수정 (활성화/비활성화/차단/삭제 등)
	@PutMapping("/{userId}/status")
	public String updatedUserStatus(@PathVariable Long userId,
									@RequestParam Status status,
									@RequestParam(required = false) Integer banDurationDays) {
		adminUserService.updateUserStatus(userId, status, banDurationDays);
		return "회원 상태가 업데이트되었습니다.";
	}
	
	// 회원 탈퇴 처리
	@DeleteMapping("/{userId}")
	public String deleteUser(@PathVariable Long userId) {
		adminUserService.deleteUser(userId);
		return "회원이 삭제되었습니다.";
	}
}
