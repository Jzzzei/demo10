package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartItemRequest;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.model.Cart;

public interface CartService {
    CartResponse getCart();
    CartResponse addToCart(CartItemRequest request);
    CartResponse updateCartItem(Long itemId, CartItemRequest request);
    void removeFromCart(Long itemId);
    void clearCart(Long userId);

    void clearCart();

    Cart getCartByUser(Long userId);
}