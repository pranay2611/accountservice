package com.mtech.accountservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mtech.accountservice.entity.CustomerProjection;
import com.mtech.accountservice.entity.CustomerProjectionEvent;
import com.mtech.accountservice.service.CustomerProjectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CustomerProjectionController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProjectionController.class);

    @Autowired
    private CustomerProjectionService customerProjectionService;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void handleCustomerEvent(String message) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Deserialize into DTO
        CustomerProjectionEvent event = objectMapper.readValue(message, CustomerProjectionEvent.class);

        CustomerProjection projection = new CustomerProjection();
        projection.setCustomerId(event.getId());
        projection.setName(event.getName());
        customerProjectionService.saveCustomerProjection(projection);

        logger.info("Processed customer projection: {}", projection);
    }
}
