package com.goorno.canigo.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// 데이터 생성, 수정 시 '누가' 했는지 자동 기록
// 시스템 로그 추적, 데이터 감사, 변경 이력 관리 등

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication == null || !authentication.isAuthenticated()) {
	        return Optional.of("anonymous");
	    }

	    return Optional.of(authentication.getName()); // 또는 사용자 ID, nickname 등
	}
}
