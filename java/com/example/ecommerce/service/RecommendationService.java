package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductRecommendationResponse;
import java.util.List;

public interface RecommendationService {
    List<ProductRecommendationResponse> getPersonalizedRecommendations(Long userId, int limit);
    List<ProductRecommendationResponse> getSimilarProducts(Long productId, int limit);
    List<ProductRecommendationResponse> getPopularProducts(String category, int limit);
    List<ProductRecommendationResponse> getFrequentlyBoughtTogether(Long productId, int limit);
} 