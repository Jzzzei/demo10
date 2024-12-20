package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.FavoriteResponse;
import com.example.ecommerce.model.Favorite;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.FavoriteRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public FavoriteResponse addFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Product already in favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        
        favorite = favoriteRepository.save(favorite);
        return convertToFavoriteResponse(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        favoriteRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public Page<FavoriteResponse> getUserFavorites(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return favoriteRepository.findByUser(user, pageable)
            .map(this::convertToFavoriteResponse);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        return favoriteRepository.existsByUserAndProduct(user, product);
    }

    private FavoriteResponse convertToFavoriteResponse(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(favorite.getId());
        response.setProductId(favorite.getProduct().getId());
        response.setProductName(favorite.getProduct().getName());
        response.setProductDescription(favorite.getProduct().getDescription());
        response.setProductPrice(favorite.getProduct().getPrice());
        response.setProductImageUrl(favorite.getProduct().getImageUrl());
        response.setProductRating(favorite.getProduct().getAverageRating());
        response.setProductReviewCount(favorite.getProduct().getReviewCount());
        response.setAddedAt(favorite.getCreatedAt());
        return response;
    }
} 