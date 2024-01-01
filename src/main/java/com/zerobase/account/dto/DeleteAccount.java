package com.zerobase.account.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DeleteAccount {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DaRequest {
		@NotNull
		@Min(1)
		private Long id;
		
		@NotBlank
		@Size(min = 10, max = 10)
		private String accountNumber;
	}
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class DaResponse {
		private Long userId;
		private String accountNumber;
		private LocalDateTime unRegisteredAt;
		
		public static DaResponse fromDto(AccountDto accountDto) {
			return DaResponse.builder()
					.userId(accountDto.getUserId())
					.accountNumber(accountDto.getAccountNumber())
					.unRegisteredAt(accountDto.getUnRegisteredAt())
					.build();
		}
	}

}
