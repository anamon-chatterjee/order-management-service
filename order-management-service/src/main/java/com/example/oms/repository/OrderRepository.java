package com.example.oms.repository;

import com.example.oms.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByIdAndUserId(UUID orderId, UUID currentUser);
}
