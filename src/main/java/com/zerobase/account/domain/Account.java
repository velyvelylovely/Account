package com.zerobase.account.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.zerobase.account.exception.AccountException;
import com.zerobase.account.type.AccountStatus;
import com.zerobase.account.type.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity {
	@ManyToOne
	private AccountUser accountUser;
	private String accountNumber;
	
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	private Long balance;
	
	private LocalDateTime registeredAt;
	private LocalDateTime unRegisteredAt;
	
	public void useBalance(Long amount) {
		if (amount > balance) {
			throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
		}
		
		balance -= amount;
	}
	
	public void cancelBalance(Long amount) {
		if (amount < 0) {
			throw new AccountException(ErrorCode.INVALID_REQUEST);
		}
		
		balance += amount;
	}
}
