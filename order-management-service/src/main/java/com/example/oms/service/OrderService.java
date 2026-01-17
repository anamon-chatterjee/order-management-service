package com.example.oms.service;

import com.example.oms.domain.entity.Order;
import com.example.oms.domain.enums.OrderStatus;
import com.example.oms.exception.InvalidOrderStateException;
import com.example.oms.exception.OrderNotFoundException;
import com.example.oms.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(UUID customerId, BigDecimal totalAmount) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public Order confirmOrder(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "Only CREATED orders can be confirmed"
            );
        }


        order.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }

    public Order cancelOrder(UUID orderId) {
        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Delivered orders cannot be cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order getOrder(UUID orderId) {
        log.info("Fetching order with id={}", orderId);
        return getOrderOrThrow(orderId);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
