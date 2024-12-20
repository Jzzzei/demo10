package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductRecommendationResponse;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public RecommendationServiceImpl(ProductRepository productRepository,
                                   OrderRepository orderRepository,
                                   UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ProductRecommendationResponse> getPersonalizedRecommendations(Long userId, int limit) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 获取用户的购买历史
        List<Order> userOrders = orderRepository.findByUser(user);
        
        // 获取用户购买过的商品类别
        Set<String> userCategories = userOrders.stream()
            .flatMap(order -> order.getItems().stream())
            .map(item -> item.getProduct().getCategory())
            .collect(Collectors.toSet());

        // 基于用户购买过的类别推荐相似商品
        List<Product> recommendedProducts = productRepository.findAll().stream()
            .filter(product -> userCategories.contains(product.getCategory()))
            // 排除用户已购买的商品
            .filter(product -> !userOrders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet())
                .contains(product.getId()))
            .sorted(Comparator.comparing(Product::getAverageRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());

        return convertToRecommendationResponses(recommendedProducts, "Based on purchase history");
    }

    @Override
    public List<ProductRecommendationResponse> getSimilarProducts(Long productId, int limit) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // 获取同类别的商品
        List<Product> similarProducts = productRepository.findAll().stream()
            .filter(p -> p.getCategory().equals(product.getCategory()))
            .filter(p -> !p.getId().equals(productId))
            .sorted(Comparator.comparing(Product::getAverageRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());

        return convertToRecommendationResponses(similarProducts, "Similar products");
    }

    @Override
    public List<ProductRecommendationResponse> getPopularProducts(String category, int limit) {
        List<Product> popularProducts = category == null ?
            productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getAverageRating).reversed())
                .limit(limit)
                .collect(Collectors.toList()) :
            productRepository.findAll().stream()
                .filter(p -> p.getCategory().equals(category))
                .sorted(Comparator.comparing(Product::getAverageRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return convertToRecommendationResponses(popularProducts, "Popular products");
    }

    @Override
    public List<ProductRecommendationResponse> getFrequentlyBoughtTogether(Long productId, int limit) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // 获取所有包含该商品的订单
        List<Order> relatedOrders = orderRepository.findAll().stream()
            .filter(order -> order.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId)))
            .collect(Collectors.toList());

        // 统计其他商品在这些订单中出现的次数
        Map<Product, Integer> productFrequency = new HashMap<>();
        relatedOrders.forEach(order -> 
            order.getItems().stream()
                .map(OrderItem::getProduct)
                .filter(p -> !p.getId().equals(productId))
                .forEach(p -> productFrequency.merge(p, 1, Integer::sum))
        );

        // 按出现频率排序
        List<Product> frequentlyBoughtTogether = productFrequency.entrySet().stream()
            .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return convertToRecommendationResponses(frequentlyBoughtTogether, "Frequently bought together");
    }

    private List<ProductRecommendationResponse> convertToRecommendationResponses(
            List<Product> products, String recommendationType) {
        return products.stream().map(product -> {
            ProductRecommendationResponse response = new ProductRecommendationResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setDescription(product.getDescription());
            response.setPrice(product.getPrice());
            response.setImageUrl(product.getImageUrl());
            response.setAverageRating(product.getAverageRating());
            response.setReviewCount(product.getReviewCount());
            response.setRecommendationType(recommendationType);
            // 这里可以根据不同的推荐类型计算推荐分数
            response.setRecommendationScore(product.getAverageRating());
            return response;
        }).collect(Collectors.toList());
    }
} 