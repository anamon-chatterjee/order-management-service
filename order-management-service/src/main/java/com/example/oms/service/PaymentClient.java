package com.example.oms.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class PaymentClient {

    private final PaymentService paymentService;

    public PaymentClient(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Retry(name = "paymentRetry", fallbackMethod = "fallback")
    @CircuitBreaker(name = "paymentCircuit", fallbackMethod = "fallback")
    public String executePayment(String orderId) {
        return paymentService.processPayment(orderId);
    }

    public String fallback(String orderId, Throwable ex) {
        return "Payment service temporarily unavailable. Order placed in PENDING state.";
    }
}