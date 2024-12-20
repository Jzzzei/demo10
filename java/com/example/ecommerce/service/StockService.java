package com.example.ecommerce.service;

import com.example.ecommerce.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface StockService {
    StockMovement addStock(Long productId, Integer quantity, String note);
    StockMovement reduceStock(Long productId, Integer quantity, String note);
    StockMovement adjustStock(Long productId, Integer newQuantity, String note);
    Page<StockMovement> getProductStockMovements(Long productId, Pageable pageable);
    List<StockMovement> getProductStockMovementsByDateRange(
        Long productId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    Integer getCurrentStock(Long productId);
    void validateStock(Long productId, Integer requiredQuantity);
} 