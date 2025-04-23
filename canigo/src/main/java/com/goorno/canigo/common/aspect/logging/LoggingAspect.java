package com.goorno.canigo.common.aspect.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j		// 로그 출력을 위함
@Aspect		// 이 클래스가 Aspect(관심사 분리 대상)임을 명시
@Component	// 스프링 빈으로 등록
public class LoggingAspect {

	// 컨트롤러, 서비스 패키지 내 모든 메서드에 대해 AOP Around 적용
	// @Around 포인트컷은 가장 강력한 AOP 방식으로,
	// 메서드 실행 전후, 예외까지 전부 제어 가능
	@Around("execution(* com.goorno.canigo.controller..*(..)) || execution(* com.goorno.canigo.service..*(..)) || execution(* com.goorno.canigo.admin.controller..*(..)) || execution(* com.goorno.canigo.admin.service..*(..))")
	public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		
		// joinPoint: 어디에서 어떤 동작을 가로채는지를 나타내는 객체
		
		long start = System.currentTimeMillis(); // 시간 측정
		
		// 메서드 이름과 클래스 이름 추출
		// joinPoint.getSignature(): 지금 실행하려는 메서드에 대한 시그니처 정보
		//							 시그니처: 메서드의 이름, 파라미터 타입 등
		// joinPoint.getTarget(): AOP가 감싸고 있는 원래 대상 객체,
		//						  진짜 실행될 서비스나 컨트롤러의 인스턴스
		String methodName = joinPoint.getSignature().toShortString();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		Object[] args = joinPoint.getArgs(); // 전달된 인자들 가져오기
		
		// 메서드 호출 로그 출력
		log.info("➡️ [{}} 호출됨 - 파라미터: {}", className + "." + methodName, argsToString(args));
		
		Object result = null;
		try {
			// 실제 대상 메서드 실행
			result = joinPoint.proceed();
			// 실행 시간 계산
			long duration = System.currentTimeMillis() - start;
			// 정상 수행 로그 출력
			log.info("✅ [{}] 완료됨 - 실행시간: {}ms - 반환값: {}", className + "." + methodName, duration, result);
			// 결과 반환
			return result;
		} catch (Throwable ex) {
			// 예외 발생 시 로그 출력
			log.error("❌ [{}] 예외 발생: {}", className + "." + methodName, ex.getMessage(), ex);
			// 예외 다시 던지기(예외 삼키지 않음)
			throw ex;
		}
	}
	
	// 파라미터들을 문자열로 변환하는 유틸 메서드
	private String argsToString(Object[] args) {
		// 인자가 없는 경우
		if (args == null || args.length == 0) return "없음";
		
		StringBuilder sb = new StringBuilder();
		for (Object arg : args) {
			sb.append(arg).append(", "); // 각 인자 붙이기
		}
		// 마지막 ", " 제거하고 반환
		return sb.substring(0, sb.length() - 2); 
	}
}
