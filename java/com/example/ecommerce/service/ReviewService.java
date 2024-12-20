package com.example.ecommerce.service;

import com.example.ecommerce.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review createReview(Long userId, Long productId, Integer rating, String comment);
    Review updateReview(Long reviewId, Long userId, Integer rating, String comment);
    void deleteReview(Long reviewId, Long userId);
    Review getReviewById(Long id);
    Page<Review> getProductReviews(Long productId, Pageable pageable);
    Page<Review> getUserReviews(Long userId, Pageable pageable);
    void verifyPurchaseAndUpdateReview(Long userId, Long productId);
    Double calculateAverageRating(Long productId);
} 