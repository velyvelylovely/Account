package com.zerobase.account.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.BDDMockito.given;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zerobase.account.dto.UseBalance;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.type.ErrorCode;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {
	@Mock
	private LockService lockService;

	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;
	
	@InjectMocks
	private LockAopAspect lockAopAspect;
	
	@Test
	void lockAndUnLock() throws Throwable {
		// given
		ArgumentCaptor<String> lockArgumentCaptor = 
				ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> unLockArgumentCaptor =
				ArgumentCaptor.forClass(String.class);
		UseBalance.UbRequest request =
				new UseBalance.UbRequest(123L, "1234", 10000L);

		// when
		lockAopAspect.aroundMethod(proceedingJoinPoint, request);

		// then
		verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
		verify(lockService, times(1)).unlock(unLockArgumentCaptor.capture());
		assertEquals("1234", lockArgumentCaptor.getValue());
		assertEquals("1234", unLockArgumentCaptor.getValue());
	}
	
	@Test
	void lockAndUnLock_evenIfThrow() throws Throwable {
		// given
		ArgumentCaptor<String> lockArgumentCaptor = 
				ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> unLockArgumentCaptor =
				ArgumentCaptor.forClass(String.class);
		UseBalance.UbRequest request =
				new UseBalance.UbRequest(123L, "54321", 1000L);
		
        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

		// when
        assertThrows(AccountException.class, () ->
                lockAopAspect.aroundMethod(proceedingJoinPoint, request));

		// then
		verify(lockService, times(1)).lock(lockArgumentCaptor.capture());
		verify(lockService, times(1)).unlock(unLockArgumentCaptor.capture());
		assertEquals("54321", lockArgumentCaptor.getValue());
		assertEquals("54321", unLockArgumentCaptor.getValue());
	}
}
