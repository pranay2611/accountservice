package com.mtech.accountservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {

    private final RestTemplate restTemplate;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public CustomerService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean isCustomerPresent(Long customerId) {
        String customerServiceUrl = this.customerServiceUrl + "/" + customerId;
        try {
            restTemplate.getForObject(customerServiceUrl, Object.class);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
