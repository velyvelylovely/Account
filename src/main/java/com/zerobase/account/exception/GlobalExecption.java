package com.zerobase.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zerobase.account.dto.ErrorResponse;
import com.zerobase.account.type.ErrorCode;

@RestControllerAdvice
public class GlobalExecption {
	@ExceptionHandler(AccountException.class)
	public ResponseEntity<ErrorResponse> handleAccountException(AccountException e) {
		HttpStatus status = null;
		
		status = getHttpStatus(e);
		
		return ResponseEntity
				.status(status)
				.body(new ErrorResponse(e.getErrorCode(), e.getErrorMessage()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handlerMethodArgumentNotValidException(
			MethodArgumentNotValidException e) {
	
		return new ErrorResponse(
			ErrorCode.INVALID_REQUEST,
			ErrorCode.INVALID_REQUEST.getDescription());
	}

	private HttpStatus getHttpStatus(AccountException e) {
		HttpStatus status = HttpStatus.OK;

//		if (ErrorCode.USER_NOT_FOUND.equals(e.getErrorCode())
//			|| ErrorCode.ACCOUNT_NOT_FOUND.equals(e.getErrorCode())
//			|| ErrorCode.USER_ACCOUNT_UNMATCH.equals(e.getErrorCode())) {
//			status = HttpStatus.NOT_FOUND;
//		} else if (ErrorCode.MAX_COUNT_PER_USER_10.equals(e.getErrorCode())) {
//			status = HttpStatus.NOT_ACCEPTABLE;
//		} else if (ErrorCode.ACCOUNT_ALREADY_UNREGISTERED.equals(e.getErrorCode())) {
//			status = HttpStatus.BAD_REQUEST;
//		}

		return status;
	}
}
