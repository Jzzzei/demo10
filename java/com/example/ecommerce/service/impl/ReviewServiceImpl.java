package com.example.ecommerce.service.impl;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Review createReview(Long userId, Long productId, Integer rating, String comment) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (reviewRepository.existsByProductAndUser(product, user)) {
            throw new RuntimeException("User has already reviewed this product");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        
        // 检查用户是否购买过该商品
        verifyPurchaseAndUpdateReview(userId, productId);

        Review savedReview = reviewRepository.save(review);
        updateProductRating(product);
        
        return savedReview;
    }

    @Override
    public Review updateReview(Long reviewId, Long userId, Integer rating, String comment) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this review");
        }

        review.setRating(rating);
        review.setComment(comment);
        Review updatedReview = reviewRepository.save(review);
        updateProductRating(review.getProduct());
        
        return updatedReview;
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this review");
        }

        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public Page<Review> getProductReviews(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return reviewRepository.findByProduct(product, pageable);
    }

    @Override
    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return reviewRepository.findByUser(user, pageable);
    }

    @Override
    public void verifyPurchaseAndUpdateReview(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 检查用户是否有已完成的订单包含该商品
        boolean hasPurchased = orderRepository.findByUser(user).stream()
            .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
            .flatMap(order -> order.getItems().stream())
            .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (hasPurchased) {
            Review review = reviewRepository.findByProductAndUser(
                productRepository.findById(productId).get(), user)
                .orElse(null);
            if (review != null) {
                review.setVerified(true);
                reviewRepository.save(review);
            }
        }
    }

    @Override
    public Double calculateAverageRating(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return product.getReviews().stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
    }

    private void updateProductRating(Product product) {
        Double avgRating = calculateAverageRating(product.getId());
        product.setAverageRating(avgRating);
        product.setReviewCount(product.getReviews().size());
        productRepository.save(product);
    }
} 