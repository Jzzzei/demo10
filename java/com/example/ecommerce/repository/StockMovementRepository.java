package com.example.ecommerce.repository;

import com.example.ecommerce.model.StockMovement;
import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    Page<StockMovement> findByProduct(Product product, Pageable pageable);
    List<StockMovement> findByProductAndTimestampBetween(
        Product product, 
        LocalDateTime start, 
        LocalDateTime end
    );
} 