package com.example.ecommerce.dto;

import com.example.ecommerce.model.StockMovement;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockMovementResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private Integer quantityChanged;
    private LocalDateTime timestamp;
    private StockMovement.MovementType type;
    private String note;
} 