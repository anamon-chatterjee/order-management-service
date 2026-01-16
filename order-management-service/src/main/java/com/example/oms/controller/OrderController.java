package com.example.oms.controller;

import com.example.oms.domain.entity.Order;
import com.example.oms.dto.CreateOrderRequest;
import com.example.oms.dto.OrderResponse;
import com.example.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.customerId(),
                request.totalAmount()
        );
        return toResponse(order);
    }

    @PostMapping("/{id}/confirm")
    public OrderResponse confirmOrder(@PathVariable UUID id) {
        return toResponse(orderService.confirmOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable UUID id) {
        return toResponse(orderService.cancelOrder(id));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        return toResponse(orderService.getOrder(id));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
