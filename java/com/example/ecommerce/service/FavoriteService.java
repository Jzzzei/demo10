package com.example.ecommerce.service;

import com.example.ecommerce.dto.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    FavoriteResponse addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
    Page<FavoriteResponse> getUserFavorites(Long userId, Pageable pageable);
    boolean isFavorite(Long userId, Long productId);
} 