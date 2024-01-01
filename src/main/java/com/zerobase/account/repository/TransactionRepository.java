package com.zerobase.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zerobase.account.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	Optional<Transaction> findByTransactionId(String transactionId);
}
