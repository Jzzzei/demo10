package com.example.ecommerce.dto;

import com.example.ecommerce.model.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private AddressDTO shippingAddress;
    private String paymentId;
    private String trackingNumber;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }

    @Data
    public static class AddressDTO {
        private Long id;
        private String street;
        private String city;
        private String state;
        private String country;
        private String zipCode;
    }
} 