package com.zerobase.account.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.account.dto.AccountDto;
import com.zerobase.account.dto.CreateAccount;
import com.zerobase.account.dto.DeleteAccount;
import com.zerobase.account.service.AccountService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

@WebMvcTest(AccountController.class)
@DisplayName("계좌 관련 컨트롤러 테스트")
class AccountControllerTest {
	final String uriPrefix = "/api/v1";
	@MockBean
	private AccountService accountService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("계좌생성 성공")
	void successCreateAccount() throws Exception {
		// given
		given(accountService.createAccount(anyLong(), anyLong()))
			.willReturn(AccountDto.builder()
					.userId(1L)
					.accountNumber("1000000000")
					.build());
		// when
		// then
		mockMvc.perform(post(uriPrefix + "/account")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new CreateAccount.CaRequest(3333L, 1111L)
				)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.userId").value(1))
		.andExpect(jsonPath("$.accountNumber").value("1000000000"));
	}
	
	@Test
	@DisplayName("유저 계좌 목록 조회")
	void successGetAccountByUserId() throws Exception {
		// given
		given(accountService.getAccountsByUserId(anyLong()))
			.willReturn(Arrays.asList(
				AccountDto.builder()
					.accountNumber("1000000000")
					.balance(1000L)
					.build(),
				AccountDto.builder()
					.accountNumber("1000000001")
					.balance(1001L)
					.build(),
				AccountDto.builder()
					.accountNumber("1000000002")
					.balance(1002L)
					.build()
			));
		
		// when
		// then
		mockMvc.perform(get(uriPrefix + "/account?user_id=1"))
			.andDo(print())
			.andExpect(jsonPath("$[0].accountNumber").value("1000000000"))
			.andExpect(jsonPath("$[0].balance").value(1000L))
			.andExpect(jsonPath("$[1].accountNumber").value("1000000001"))
			.andExpect(jsonPath("$[1].balance").value(1001L))
			.andExpect(jsonPath("$[2].accountNumber").value("1000000002"))
			.andExpect(jsonPath("$[2].balance").value(1002L))
			;
	}
	
	@Test
	@DisplayName("계좌 해지 성공")
	void successDeleteAccount() throws Exception {
		// given
		given(accountService.deleteAccount(anyLong(), anyString()))
			.willReturn(AccountDto.builder()
					.userId(1L)
					.accountNumber("1000000000")
					.unRegisteredAt(LocalDateTime.now())
					.build());
		// when
		// then
		mockMvc.perform(delete(uriPrefix + "/account")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new DeleteAccount.DaRequest(1L, "1000000000")
				)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.userId").value(1L))
		.andExpect(jsonPath("$.accountNumber").value("1000000000"))
		;
	}

}
