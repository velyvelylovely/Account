package com.zerobase.account.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.zerobase.account.type.TransactionResultType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UseBalance {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UbRequest {
		@NotNull
		@Min(1)
		private Long userId;
		
		@NotBlank
		@Size(min =  10, max = 10)
		private String accountNumber;
		
		@NotNull
		@Min(0)
		@Max(1_000_000_000)
		private Long amount;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class UbResponse {
		private String accountNumber;
		private TransactionResultType transactionResult;
		private String transactionId;
		private Long amount;
		private LocalDateTime transactionAt;
		
		public static UbResponse fromDto(TransactionDto transactionDto) {
			return UbResponse.builder()
				.accountNumber(transactionDto.getAccountNumber())
				.transactionResult(transactionDto.getTransactionResultType())
				.transactionId(transactionDto.getTransactionId())
				.amount(transactionDto.getAmount())
				.transactionAt(transactionDto.getTransactedAt())
				.build();
		}
	}
	
}
