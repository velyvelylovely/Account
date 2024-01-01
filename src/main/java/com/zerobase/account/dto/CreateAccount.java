package com.zerobase.account.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccount {
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CaRequest {
		@NotNull
		@Min(1)
		private Long id;
		
		@NotNull
		@Min(0)
		private Long initialBalance;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CaResponse {
		private Long userId;
		private String accountNumber;
		private LocalDateTime registeredAt;
		
		public static CaResponse fromDto(AccountDto accountDto) {
			return CaResponse.builder()
					.userId(accountDto.getUserId())
					.accountNumber(accountDto.getAccountNumber())
					.registeredAt(accountDto.getRegisteredAt())
					.build();
		}
	}
}
