package com.example.oms.controller;

import com.example.oms.domain.entity.Order;
import com.example.oms.dto.CreateOrderRequest;
import com.example.oms.dto.OrderResponse;
import com.example.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.customerId(),
                request.totalAmount()
        );
        return toResponse(order);
    }

    @PreAuthorize("hasAnyRole('OPS','ADMIN')")
    @PostMapping("/{id}/confirm")
    public OrderResponse confirmOrder(@PathVariable("id") UUID id) {
        return toResponse(orderService.confirmOrder(id));
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
            "hasRole('CUSTOMER') and @orderSecurity.isOwner(#id)"
    )
    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable("id") UUID id) {
        return toResponse(orderService.cancelOrder(id));
    }

    @PreAuthorize(
            "hasRole('ADMIN') or " +
            "(hasRole('CUSTOMER') and @orderSecurity.isOwner(#id))"
    )
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable("id") UUID id) {
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
