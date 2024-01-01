package com.zerobase.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zerobase.account.domain.Account;
import com.zerobase.account.domain.AccountUser;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	Integer countByAccountUser(AccountUser accountUser);
	
	Optional<Account> findFirstByOrderByIdDesc();
	
	List<Account> findByAccountUser(AccountUser accountUser);

	Optional<Account> findByAccountNumber(String AccountNumber);
}
