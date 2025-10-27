package com.mtech.accountservice.repository;

import com.mtech.accountservice.entity.CustomerProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProjectionRepository extends JpaRepository<CustomerProjection, Long> {
}
