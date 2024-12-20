package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRecommendationResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Double averageRating;
    private Integer reviewCount;
    private Double recommendationScore;  // 推荐分数
    private String recommendationType;   // 推荐类型（例如：基于购买历史、基于浏览历史、热门商品等）
} 