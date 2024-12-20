package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, Long shippingAddressId);
    Order getOrderById(Long id);
    List<Order> getUserOrders(Long userId);
    Page<Order> getUserOrders(Long userId, Pageable pageable);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    void processPayment(Long orderId, String paymentMethod);
    void updateTrackingNumber(Long orderId, String trackingNumber);
} 