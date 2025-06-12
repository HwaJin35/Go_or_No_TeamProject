//package com.goorno.canigo.test;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import com.goorno.canigo.repository.RefreshTokenRepository;
//
//import lombok.RequiredArgsConstructor;
////서버 재시작시 리프레시 토큰 전부 삭제할 필요가 있는 경우 사용
//@Component
//@RequiredArgsConstructor
//public class RefreshTokenCleanupRunner implements CommandLineRunner {
//
//    private final RefreshTokenRepository refreshTokenRepository;
//
//    @Override
//    public void run(String... args) {
//        refreshTokenRepository.deleteAll();
//        System.out.println("✅ 서버 재시작 → RefreshToken 전부 삭제 완료");
//    }
//}