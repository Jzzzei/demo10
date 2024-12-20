package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductRecommendationResponse;
import com.example.ecommerce.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        // 这里需要从用户名获取用户ID，可以通过UserService实现
        // 暂时返回1L作为示例
        return 1L;
    }

    @GetMapping("/personalized")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductRecommendationResponse>> getPersonalizedRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
            recommendationService.getPersonalizedRecommendations(getCurrentUserId(), limit)
        );
    }

    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<ProductRecommendationResponse>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
            recommendationService.getSimilarProducts(productId, limit)
        );
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductRecommendationResponse>> getPopularProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
            recommendationService.getPopularProducts(category, limit)
        );
    }

    @GetMapping("/frequently-bought-together/{productId}")
    public ResponseEntity<List<ProductRecommendationResponse>> getFrequentlyBoughtTogether(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(
            recommendationService.getFrequentlyBoughtTogether(productId, limit)
        );
    }
} 