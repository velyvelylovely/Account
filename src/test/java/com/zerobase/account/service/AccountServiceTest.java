package com.zerobase.account.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("계좌 관련 서비스 테스트")
class AccountServiceTest {
	private final int maxAccountCount = 10;

	@Mock
	private AccountRepository accountRepository;
	
	@Mock
	private AccountUserRepository accountUserRepository;
	
	@InjectMocks
	private AccountService accountService;
	
	@Test
	@DisplayName("계좌 생성 - 계좌 생성 성공")
	void createAccount_success() {
		// given
		AccountUser accountUser = AccountUser.builder()
			.name("Kim")
			.build();
		accountUser.setId(1L);
		
		Account account = Account.builder()
						.accountNumber("1000000000")
						.build();

		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser));
		
		given(accountRepository.findFirstByOrderByIdDesc())
			.willReturn(Optional.of(account));
		
		given(accountRepository.save(any()))
			.willReturn(Account.builder()
					.accountUser(accountUser)
					.accountNumber(
						Integer.parseInt(account.getAccountNumber()) + 1 + "")
					.build());
		
		ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(
				Account.class);
		
		// when
		AccountDto accountDto = accountService.createAccount(1L, 1000L);

		// then
		verify(accountRepository, times(1)).save(accountCaptor.capture());
		assertEquals(1L, accountDto.getUserId());
		assertEquals(
			Integer.parseInt(account.getAccountNumber()) + 1 + "",
			accountCaptor.getValue().getAccountNumber());
	}
	
	@Test
	@DisplayName("계좌 생성 - 해당 유저 없음")
	void createAccount_userNotFound() {
		// given
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.createAccount(1L, 1000L));
		
		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌 생성 - 유저 당 최대 계좌는 10개")
	void createAccount_maxAccountIs10() {
		// given
		AccountUser accountUser = AccountUser.builder()
			.name("Kim")
			.build();
		accountUser.setId(1L);
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser));
		
		given(accountRepository.countByAccountUser(accountUser))
			.willReturn(maxAccountCount);

		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.createAccount(1L, 1000L));

		// then
		assertEquals(ErrorCode.MAX_COUNT_PER_USER_10, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌 정보 조회 - 해당 유저 없음")
	void getAccount_userNotFound() {
		// given
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.empty());
		
		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.getAccountsByUserId(1L));

		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌 정보 조회 - 해당 유저 계좌 목록 조회")
	void getAccount_accountsByUserId() {
		// given
		AccountUser accountUser = AccountUser.builder()
					.name("Kim")
					.build();
		accountUser.setId(1L);
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser));
		
		given(accountRepository.findByAccountUser(accountUser))
			.willReturn(Arrays.asList(
				Account.builder()
					.accountUser(accountUser)
					.accountNumber("1000000000")
					.balance(1000L)
					.build(),	
				Account.builder()
					.accountUser(accountUser)
					.accountNumber("1000000001")
					.balance(1001L)
					.build(),	
				Account.builder()
					.accountUser(accountUser)
					.accountNumber("1000000002")
					.balance(1002L)
					.build()
			));

		// when
		List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

		// then
		assertEquals(3, accountDtos.size());
		assertEquals("1000000000", accountDtos.get(0).getAccountNumber());
		assertEquals(1000L, accountDtos.get(0).getBalance());
		assertEquals("1000000001", accountDtos.get(1).getAccountNumber());
		assertEquals(1001L, accountDtos.get(1).getBalance());
		assertEquals("1000000002", accountDtos.get(2).getAccountNumber());
		assertEquals(1002L, accountDtos.get(2).getBalance());
	}
	
	@Test
	@DisplayName("계좌 해지 - 유저 계좌 해지")
	void deleteAccount_success() {
		// given
		AccountUser accountUser = AccountUser.builder()
					.name("Kim")
					.build();
		accountUser.setId(1L);
		
		Account account = Account.builder()
					.accountUser(accountUser)
					.accountNumber("1000000000")
					.balance(0L)
					.accountStatus(AccountStatus.IN_USE)
					.build();
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser))
		;
		
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.of(account))
		;
		
		ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

		// when
		accountService.deleteAccount(1L, "1000000000");
		
		// then
		verify(accountRepository, times(1)).save(captor.capture());
		assertEquals(1L, captor.getValue().getAccountUser().getId());
		assertEquals("1000000000", captor.getValue().getAccountNumber());
		assertEquals(AccountStatus.UNREGISTERD, captor.getValue().getAccountStatus());
	}
	
	@Test
	@DisplayName("계좌 해지 - 해당 유저 없음")
	void deleteAccount_userNotFound() {
		// given
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.empty())
		;

		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.deleteAccount(1L, "1000000000"));
		
		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌해지 - 해당 계좌 없음")
	void deleteAccount_accountNotFound() {
		// given
		AccountUser accountUser = AccountUser.builder()
					.name("Kim")
					.build();
		accountUser.setId(1L);
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser))
		;
		
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.empty())
		;
		
		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.deleteAccount(1L, "1000000000"));

		// then
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌해지 - 계좌 소유주 다름")
	void deleteAccount_userAccountUnMatch() {
		// given
		AccountUser accountKim = AccountUser.builder()
							.name("Kim")
							.build();
		accountKim.setId(1L);
		
		AccountUser accountLee = AccountUser.builder()
					.name("Lee")
					.build();
		accountLee.setId(2L);
		
		Account account = Account.builder()
					.accountUser(accountLee)
					.build();
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountKim))
		;
		
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.of(account))
		;
		
		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.deleteAccount(1L, "1000000000"));
		
		// then
		assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌해지 - 이미 해지된 계좌 해지 시도")
	void deleteAccount_accountAlreadyUnRegistered() {
		// given
		AccountUser accountUser = AccountUser.builder()
					.name("Kim")
					.build();
		accountUser.setId(1L);
		
		Account account = Account.builder()
					.accountNumber("1000000000")
					.accountUser(accountUser)
					.accountStatus(AccountStatus.UNREGISTERD)
					.build();
		
		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser))
		;
		
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.of(account))
		;
		
		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.deleteAccount(1L, "1000000000"));
		
		// then
		assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
	}
	
	@Test
	@DisplayName("계좌해지 - 계좌 잔액이 남은 경우")
	void deleteAccount_balanceNotEmpty() {
		// given
		AccountUser accountUser = AccountUser.builder()
					.name("Kim")
					.build();
		accountUser.setId(1L);
		
		Account account = Account.builder()
					.accountNumber("1000000000")
					.accountUser(accountUser)
					.balance(100L)
					.accountStatus(AccountStatus.IN_USE)
					.build();

		given(accountUserRepository.findById(anyLong()))
			.willReturn(Optional.of(accountUser))
		;
		
		given(accountRepository.findByAccountNumber(anyString()))
			.willReturn(Optional.of(account))
		;

		// when
		AccountException exception = assertThrows(AccountException.class, 
			() -> accountService.deleteAccount(1L, "1000000000"));

		// then
		assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());
	}
}
