package com.mtech.accountservice.repository;

import com.mtech.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {
}

