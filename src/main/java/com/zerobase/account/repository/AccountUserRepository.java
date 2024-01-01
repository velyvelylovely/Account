package com.zerobase.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zerobase.account.domain.AccountUser;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long>{
}
