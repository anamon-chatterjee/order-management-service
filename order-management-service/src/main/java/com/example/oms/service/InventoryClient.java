package com.example.oms.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryClient {

    @Retry(name = "inventoryRetry", fallbackMethod = "circuitFallback")
    @CircuitBreaker(name = "inventoryCircuit", fallbackMethod = "circuitFallback")
    public String checkStock(String productId) {
        //Simulating external call
        RestOperations restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                "http://inventory-service/check/" + productId,
                String.class
        );
    }

    public String circuitFallback(String productId, Throwable ex) {
        return "Inventory temporarily unavailable (Circuit Open)";
    }
}
