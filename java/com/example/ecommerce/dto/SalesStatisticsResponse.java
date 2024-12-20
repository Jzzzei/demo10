package com.example.ecommerce.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SalesStatisticsResponse {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer totalProducts;
    private Double averageOrderValue;
    
    private List<TopProductDTO> topProducts;
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, Integer> ordersByStatus;
    
    @Data
    public static class TopProductDTO {
        private Long productId;
        private String productName;
        private Integer quantitySold;
        private BigDecimal revenue;
    }
} 