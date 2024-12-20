package com.example.ecommerce.controller;

import com.example.ecommerce.dto.OrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        // 这里需要从用户名获取用户ID，可以通过UserService实现
        // 暂时返回1L作为示例
        return 1L;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrder(getCurrentUserId(), request.getShippingAddressId());
        if (request.getPaymentMethod() != null) {
            orderService.processPayment(order.getId(), request.getPaymentMethod());
        }
        return ResponseEntity.ok(convertToOrderResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(convertToOrderResponse(order));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<Order> orders = orderService.getUserOrders(getCurrentUserId());
        List<OrderResponse> responses = orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-orders/paged")
    public ResponseEntity<Page<OrderResponse>> getMyOrdersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orderPage = orderService.getUserOrders(getCurrentUserId(), PageRequest.of(page, size));
        Page<OrderResponse> responsePage = orderPage.map(this::convertToOrderResponse);
        return ResponseEntity.ok(responsePage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-date-range")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Order> orders = orderService.getOrdersByDateRange(start, end);
        List<OrderResponse> responses = orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(convertToOrderResponse(order));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/tracking")
    public ResponseEntity<OrderResponse> updateTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        orderService.updateTrackingNumber(id, trackingNumber);
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(convertToOrderResponse(order));
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setPaymentId(order.getPaymentId());
        response.setTrackingNumber(order.getTrackingNumber());

        // Convert shipping address
        if (order.getShippingAddress() != null) {
            OrderResponse.AddressDTO addressDTO = new OrderResponse.AddressDTO();
            addressDTO.setId(order.getShippingAddress().getId());
            addressDTO.setStreet(order.getShippingAddress().getStreet());
            addressDTO.setCity(order.getShippingAddress().getCity());
            addressDTO.setState(order.getShippingAddress().getState());
            addressDTO.setCountry(order.getShippingAddress().getCountry());
            addressDTO.setZipCode(order.getShippingAddress().getZipCode());
            response.setShippingAddress(addressDTO);
        }

        // Convert order items
        List<OrderResponse.OrderItemDTO> itemDTOs = order.getItems().stream()
            .map(item -> {
                OrderResponse.OrderItemDTO dto = new OrderResponse.OrderItemDTO();
                dto.setId(item.getId());
                dto.setProductId(item.getProduct().getId());
                dto.setProductName(item.getProduct().getName());
                dto.setQuantity(item.getQuantity());
                dto.setPrice(item.getPrice());
                dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                return dto;
            })
            .collect(Collectors.toList());
        response.setItems(itemDTOs);

        return response;
    }
} 