package com.zerobase.account.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.zerobase.account.dto.UseBalance.UbRequest;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAopAspect {
	private final LockService lockService;
	
	@Around("@annotation(com.zerobase.account.aop.AccountLock) && args(request)")
	public Object aroundMethod(
		ProceedingJoinPoint pjp,
		UbRequest request
	) throws Throwable {
		// lock 취득 시도
		lockService.lock(request.getAccountNumber());
		try {
			return pjp.proceed();
		} finally {
			// lock 해제 
			lockService.unlock(request.getAccountNumber());
		}
	}
}
