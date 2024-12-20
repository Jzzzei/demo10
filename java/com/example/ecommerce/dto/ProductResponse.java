package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brand;
    private String category;
    private String imageUrl;
    private Double averageRating;
    private Integer reviewCount;
} 