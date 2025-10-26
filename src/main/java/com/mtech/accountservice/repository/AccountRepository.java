package com.mtech.accountservice.repository;

import com.mtech.accountservice.entity.Account; // Added import
import com.mtech.accountservice.projection.CustomerProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a.customerId AS customerId, a.accountNumber AS name FROM Account a WHERE a.customerId = :customerId")
    CustomerProjection findCustomerProjectionByCustomerId(Long customerId);
}
