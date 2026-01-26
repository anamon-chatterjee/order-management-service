package com.example.oms.service;

import com.example.oms.domain.entity.Order;
import com.example.oms.domain.enums.OrderStatus;
import com.example.oms.exception.InvalidOrderStateException;
import com.example.oms.exception.OrderNotFoundException;
import com.example.oms.repository.OrderRepository;
import com.example.oms.security.CustomUserDetails;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();

        order.setCustomerId(user.getUserId());
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

        if (List.of(OrderStatus.DELIVERED, OrderStatus.CONFIRMED).contains(order.getStatus())) {
            throw new InvalidOrderStateException(
                    "Delivered or Confirmed orders cannot be cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @NewSpan("fetch-order")
    public Order getOrder(@SpanTag("order.id") UUID orderId) {
        log.info("Fetching order with id={}", orderId);
        return getOrderOrThrow(orderId);
    }

    private Order getOrderOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
