package com.goorno.canigo.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

// 데이터 생성, 수정 시 '누가' 했는지 자동 기록
// 시스템 로그 추적, 데이터 감사, 변경 이력 관리 등

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		// 인증 시스템이 없다면 기본 사용자 반환.
		return Optional.of("System");
		
		// Spring Security 사용 중이라면 예시:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return Optional.ofNullable(auth != null ? auth.getName() : "anonymous");
	}
}
