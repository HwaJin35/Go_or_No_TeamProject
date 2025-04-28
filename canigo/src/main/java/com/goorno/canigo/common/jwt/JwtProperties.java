package com.goorno.canigo.common.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")	// application.properties에서 jwt로 시작하는 설정을 읽어와 매핑
public class JwtProperties {

    private String secretKey;      // 시크릿 키
    private long accessTokenExpirationMinutes;  // AccessToken 만료시간(분)
    private long refreshTokenExpirationDays; // RefreshToken 만료시간(일)

}