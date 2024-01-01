package com.zerobase.account.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountUserRepository  accountUserRepository;
	
	@Transactional
	public AccountDto createAccount(Long userId, Long initialBalance) {
		AccountUser accountUser = getAccountUser(userId);

		validateCreateAccount(accountUser);
		
		String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
				.map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
				.orElse("1000000000");
		
		return AccountDto.fromEntity(accountRepository.save(Account.builder()
				.accountUser(accountUser)
				.accountStatus(AccountStatus.IN_USE)
				.accountNumber(newAccountNumber)
				.balance(initialBalance)
				.registeredAt(LocalDateTime.now())
				.build()));
	}

	private void validateCreateAccount(AccountUser accountUser) {
		if (accountRepository.countByAccountUser(accountUser) >= 10) {
			throw new AccountException(ErrorCode.MAX_COUNT_PER_USER_10);
		}
	}

	private AccountUser getAccountUser(Long userId) {
		AccountUser accountUser = accountUserRepository.findById(userId)
			.orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
		return accountUser;
	}
	
	@Transactional
	public List<AccountDto> getAccountsByUserId(Long userId) {
		AccountUser accountUser = getAccountUser(userId);
		
		List<Account> accounts = accountRepository.findByAccountUser(accountUser);
		
		return accounts.stream()
				.map(AccountDto::fromEntity)
				.collect(Collectors.toList());
	}
	
	@Transactional
	public AccountDto deleteAccount(Long userId, String accountNumber) {
		AccountUser accountUser = getAccountUser(userId);
		
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateDeleteAccount(accountUser, account);
		
		account.setAccountStatus(AccountStatus.UNREGISTERD);
		account.setUnRegisteredAt(LocalDateTime.now());
		
		accountRepository.save(account);
		
		return AccountDto.fromEntity(account);
	}

	private void validateDeleteAccount(AccountUser accountUser,
			Account account) {
		if(!Objects.equal(accountUser.getId(), account.getAccountUser().getId())) {
			throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
		}
		
		if(account.getAccountStatus().equals(AccountStatus.UNREGISTERD)) {
			throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
		}
		
		if(account.getBalance() > 0) {
			throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
		}
	}

}
