package com.example.ecommerce.controller;

import com.example.ecommerce.dto.StockMovementResponse;
import com.example.ecommerce.dto.StockUpdateRequest;
import com.example.ecommerce.model.StockMovement;
import com.example.ecommerce.service.StockService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")  // 只有管理员可以访问库存管理功能
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/add")
    public ResponseEntity<StockMovementResponse> addStock(@Valid @RequestBody StockUpdateRequest request) {
        StockMovement movement = stockService.addStock(
            request.getProductId(),
            request.getQuantity(),
            request.getNote()
        );
        return ResponseEntity.ok(convertToStockMovementResponse(movement));
    }

    @PostMapping("/reduce")
    public ResponseEntity<StockMovementResponse> reduceStock(@Valid @RequestBody StockUpdateRequest request) {
        StockMovement movement = stockService.reduceStock(
            request.getProductId(),
            request.getQuantity(),
            request.getNote()
        );
        return ResponseEntity.ok(convertToStockMovementResponse(movement));
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockMovementResponse> adjustStock(@Valid @RequestBody StockUpdateRequest request) {
        StockMovement movement = stockService.adjustStock(
            request.getProductId(),
            request.getQuantity(),
            request.getNote()
        );
        return ResponseEntity.ok(convertToStockMovementResponse(movement));
    }

    @GetMapping("/product/{productId}/movements")
    public ResponseEntity<Page<StockMovementResponse>> getProductStockMovements(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StockMovement> movementPage = stockService.getProductStockMovements(
            productId,
            PageRequest.of(page, size)
        );
        Page<StockMovementResponse> responsePage = movementPage.map(this::convertToStockMovementResponse);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/product/{productId}/movements/by-date")
    public ResponseEntity<List<StockMovementResponse>> getProductStockMovementsByDateRange(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<StockMovement> movements = stockService.getProductStockMovementsByDateRange(productId, start, end);
        List<StockMovementResponse> responses = movements.stream()
            .map(this::convertToStockMovementResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/product/{productId}/current")
    public ResponseEntity<Integer> getCurrentStock(@PathVariable Long productId) {
        Integer currentStock = stockService.getCurrentStock(productId);
        return ResponseEntity.ok(currentStock);
    }

    private StockMovementResponse convertToStockMovementResponse(StockMovement movement) {
        StockMovementResponse response = new StockMovementResponse();
        response.setId(movement.getId());
        response.setProductId(movement.getProduct().getId());
        response.setProductName(movement.getProduct().getName());
        response.setQuantityBefore(movement.getQuantityBefore());
        response.setQuantityAfter(movement.getQuantityAfter());
        response.setQuantityChanged(movement.getQuantityChanged());
        response.setTimestamp(movement.getTimestamp());
        response.setType(movement.getType());
        response.setNote(movement.getNote());
        return response;
    }
} 