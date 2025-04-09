package com.goorno.canigo.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

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
