package com.example.ecommerce.repository;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct(Product product, Pageable pageable);
    Page<Review> findByUser(User user, Pageable pageable);
    Optional<Review> findByProductAndUser(Product product, User user);
    boolean existsByProductAndUser(Product product, User user);
} 