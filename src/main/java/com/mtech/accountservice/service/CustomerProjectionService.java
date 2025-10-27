package com.mtech.accountservice.service;

import com.mtech.accountservice.entity.CustomerProjection;
import com.mtech.accountservice.repository.CustomerProjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerProjectionService {

    @Autowired
    private CustomerProjectionRepository customerProjectionRepository;

    public void saveCustomerProjection(CustomerProjection customerProjection) {
        customerProjectionRepository.save(customerProjection);
    }
}
