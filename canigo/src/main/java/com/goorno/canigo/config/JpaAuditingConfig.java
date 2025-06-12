package com.goorno.canigo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 이 클래스는 단순히 JPA Auditing을 활성화하는 역할.
// 마커 인터페이스와 유사한 형태
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // 생략 시 날짜가 null로 뜸
public class JpaAuditingConfig { }
