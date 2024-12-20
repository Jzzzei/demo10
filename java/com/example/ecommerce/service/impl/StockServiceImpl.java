package com.example.ecommerce.service.impl;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.StockMovement;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.StockMovementRepository;
import com.example.ecommerce.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockServiceImpl implements StockService {
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public StockServiceImpl(StockMovementRepository stockMovementRepository,
                          ProductRepository productRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    @Override
    public StockMovement addStock(Long productId, Integer quantity, String note) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantityBefore(product.getQuantity());
        movement.setQuantityChanged(quantity);
        movement.setQuantityAfter(product.getQuantity() + quantity);
        movement.setTimestamp(LocalDateTime.now());
        movement.setType(StockMovement.MovementType.STOCK_IN);
        movement.setNote(note);

        product.setQuantity(product.getQuantity() + quantity);
        productRepository.save(product);

        return stockMovementRepository.save(movement);
    }

    @Override
    public StockMovement reduceStock(Long productId, Integer quantity, String note) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantityBefore(product.getQuantity());
        movement.setQuantityChanged(-quantity);
        movement.setQuantityAfter(product.getQuantity() - quantity);
        movement.setTimestamp(LocalDateTime.now());
        movement.setType(StockMovement.MovementType.STOCK_OUT);
        movement.setNote(note);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        return stockMovementRepository.save(movement);
    }

    @Override
    public StockMovement adjustStock(Long productId, Integer newQuantity, String note) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        int quantityChanged = newQuantity - product.getQuantity();

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantityBefore(product.getQuantity());
        movement.setQuantityChanged(quantityChanged);
        movement.setQuantityAfter(newQuantity);
        movement.setTimestamp(LocalDateTime.now());
        movement.setType(StockMovement.MovementType.ADJUSTMENT);
        movement.setNote(note);

        product.setQuantity(newQuantity);
        productRepository.save(product);

        return stockMovementRepository.save(movement);
    }

    @Override
    public Page<StockMovement> getProductStockMovements(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return stockMovementRepository.findByProduct(product, pageable);
    }

    @Override
    public List<StockMovement> getProductStockMovementsByDateRange(
            Long productId, 
            LocalDateTime start, 
            LocalDateTime end) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return stockMovementRepository.findByProductAndTimestampBetween(product, start, end);
    }

    @Override
    public Integer getCurrentStock(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getQuantity();
    }

    @Override
    public void validateStock(Long productId, Integer requiredQuantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getQuantity() < requiredQuantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
    }
} 