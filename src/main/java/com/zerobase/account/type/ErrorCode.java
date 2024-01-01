package com.zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	USER_NOT_FOUND("사용자가 없습니다."),
	USER_ACCOUNT_UNMATCH("사용자와 계좌의 소유주가 다릅니다."),
	MAX_COUNT_PER_USER_10("사용자 최대 계좌는 10개입니다."),
	ACCOUNT_NOT_FOUND("계좌가 없습니다."),
	ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
	ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용 중입니다."),
	AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액보다 큽니다."),
	AMOUNT_TOO_SMALL("거래 금액이 너무 작습니다."),
	AMOUNT_TOO_BIG("거래 금액이 너무 큽니다."),
	BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
	CANCEL_AMOUNT_UNMATCH("취소 금액이 다릅니다."),
	INVALID_REQUEST("잘못된 요청입니다."),
	TRANSACTION_NOT_FOUND("해당 거래가 없습니다.")
	;
	
	private String description;
}
