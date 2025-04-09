package com.goorno.canigo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // 생략 시 날짜가 null로 뜸
public class CanigoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanigoApplication.class, args);
	}

}
