package com.example.oms.security;

import com.example.oms.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean isOwner(UUID orderId) {
        UUID currentUser = SecurityUtils.currentUserId();
        return orderRepository.existsByIdAndCustomerId(orderId, currentUser);
    }
}

