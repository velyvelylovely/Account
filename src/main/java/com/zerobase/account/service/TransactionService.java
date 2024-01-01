package com.zerobase.account.service;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;
import com.zerobase.account.domain.Transaction;
import com.zerobase.account.dto.TransactionDto;
import com.zerobase.account.exception.AccountException;
import com.zerobase.account.repository.AccountRepository;
import com.zerobase.account.repository.AccountUserRepository;
import com.zerobase.account.repository.TransactionRepository;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;
import com.zerobase.account.type.TransactionResultType;
import com.zerobase.account.type.TransactionType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountUserRepository accountUserRepository;
	private final AccountRepository accountRepository;
	private final Long minAmount = (long) 10L;
	private final Long maxAmount = (long) 100_000_000L;
	
	@Transactional
	public TransactionDto useBalance(
		Long userId, String accountNumber, Long amount
	) {
		AccountUser accountUser = accountUserRepository.findById(userId)
			.orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));
		
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateUseBalance(amount, accountUser, account);
		
		account.useBalance(amount);
		
		return TransactionDto.fromEntity(saveAndGetTransaction(
				TransactionType.USE,
				TransactionResultType.S,
				amount, 
				account));
	}

	private Transaction saveAndGetTransaction(
			TransactionType transactionType,
			TransactionResultType transactionResultType,
			Long amount, Account account) {
		return transactionRepository.save(
				Transaction.builder()
					.transactionType(transactionType)
					.transactionResultType(transactionResultType)
					.account(account)
					.amount(amount)
					.balanceSnapshot(account.getBalance())
					.transactionId(UUID.randomUUID().toString().replace("-", ""))
					.transactedAt(LocalDateTime.now())
					.build()
		);
	}

	private void validateUseBalance(Long amount, AccountUser accountUser,
			Account account) {
		if(!Objects.equal(accountUser.getId(), account.getAccountUser().getId())) {
			throw new AccountException(ErrorCode.USER_ACCOUNT_UNMATCH);
		}
		
		if (!Objects.equal(account.getAccountStatus(), AccountStatus.IN_USE)) {
			throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
		}
		
		if (account.getBalance() < amount) {
			throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
		}

		if (amount < minAmount) {
			throw new AccountException(ErrorCode.AMOUNT_TOO_SMALL);
		}

		if (amount >= maxAmount) {
			throw new AccountException(ErrorCode.AMOUNT_TOO_BIG);
		}
	}
	
	@Transactional
	public void saveAndFailedUseTransaction(
			TransactionType transactionType,
			TransactionResultType transactionResultType,
			String accountNumber,
			Long amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		saveAndGetTransaction(
				transactionType,
				transactionResultType,
				amount,
				account);
	}
	
	@Transactional
	public TransactionDto cancelBalance(
		String transactionId, String accountNumber, Long amount
	) {
		Transaction transaction = transactionRepository.findByTransactionId(transactionId)
			.orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));
		
		Account account = accountRepository.findByAccountNumber(accountNumber)
			.orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
		
		validateCancelBalance(amount, transaction);
		
		account.cancelBalance(amount);
		
		return TransactionDto.fromEntity(saveAndGetTransaction(
				TransactionType.CANCEL,
				TransactionResultType.S,
				amount,
				account));
	}

	private void validateCancelBalance(Long amount, Transaction transaction) {
		if(!Objects.equal(transaction.getAmount(), amount)) {
			throw new AccountException(ErrorCode.CANCEL_AMOUNT_UNMATCH);
		}
	}
	
	@Transactional
	public TransactionDto queryTransactionId(String transactionId) {
		return TransactionDto.fromEntity(
			transactionRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
		);
	}
	
}
