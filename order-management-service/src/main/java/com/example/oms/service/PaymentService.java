package com.example.oms.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    private final Random random = new Random();

    public String processPayment(String orderId) {

        int chance = random.nextInt(100);

        // 70% failure simulation
        if (chance < 70) {
            System.out.println("Payment service FAILED");
            throw new RuntimeException("Payment gateway timeout");
        }

        System.out.println("Payment service SUCCESS");
        return "Payment successful for order " + orderId;
    }
}
