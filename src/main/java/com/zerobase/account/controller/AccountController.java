package com.zerobase.account.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.dto.DeleteAccount;
import com.zerobase.account.dto.GetAccount;
import com.zerobase.account.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {
	private final AccountService accountService;
	
	@GetMapping("/account")
	public List<GetAccount> getAccountByUserId(
		@RequestParam("user_id") Long userId
	) {
		return accountService.getAccountsByUserId(userId)
				.stream()
				.map(accountDto -> GetAccount.builder()
						.accountNumber(accountDto.getAccountNumber())
						.balance(accountDto.getBalance())
						.build())
				.collect(Collectors.toList());
	}
	
	@PostMapping("/account")
	public CreateAccount.CaResponse createAccount(
		@RequestBody CreateAccount.CaRequest request
	) {
		return CreateAccount.CaResponse.fromDto(
				accountService.createAccount(
						request.getId(),
						request.getInitialBalance())
		);
	}
	
	@DeleteMapping("/account")
	public DeleteAccount.DaResponse deleteAccont (
		@RequestBody DeleteAccount.DaRequest request
	) {
		return DeleteAccount.DaResponse.fromDto(
			accountService.deleteAccount(
				request.getId(),
				request.getAccountNumber()
			)
		);
	}
}
