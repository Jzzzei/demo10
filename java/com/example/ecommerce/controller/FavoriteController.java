package com.example.ecommerce.controller;

import com.example.ecommerce.dto.FavoriteResponse;
import com.example.ecommerce.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        // 这里需要从用户名获取用户ID，可以通过UserService实现
        // 暂时返回1L作为示例
        return 1L;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavoriteResponse> addToFavorites(@PathVariable Long productId) {
        return ResponseEntity.ok(favoriteService.addFavorite(getCurrentUserId(), productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long productId) {
        favoriteService.removeFavorite(getCurrentUserId(), productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            favoriteService.getUserFavorites(getCurrentUserId(), PageRequest.of(page, size))
        );
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long productId) {
        return ResponseEntity.ok(favoriteService.isFavorite(getCurrentUserId(), productId));
    }
} 