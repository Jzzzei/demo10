package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CartItemRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository,
                         CartItemRepository cartItemRepository,
                         ProductRepository productRepository,
                         UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @Override
    public CartResponse getCart() {
        return convertToCartResponse(getOrCreateCart());
    }

    @Override
    public CartResponse addToCart(CartItemRequest request) {
        Cart cart = getOrCreateCart();
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // 检查库存
        if (product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElse(new CartItem());

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(product.getPrice());
        cartItem.calculateSubtotal();

        cart.addItem(cartItem);
        cart = cartRepository.save(cart);

        return convertToCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(Long itemId, CartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // 检查库存
        if (cartItem.getProduct().getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem.calculateSubtotal();

        Cart cart = cartItem.getCart();
        cart.recalculateTotal();
        cart = cartRepository.save(cart);

        return convertToCartResponse(cart);
    }

    @Override
    public void removeFromCart(Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        Cart cart = cartItem.getCart();
        cart.removeItem(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {

    }

    @Override
    public void clearCart() {
        Cart cart = getOrCreateCart();
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUser(Long userId) {
        return null;
    }

    private Cart getOrCreateCart() {
        User currentUser = getCurrentUser();
        return cartRepository.findByUser(currentUser)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(currentUser);
                return cartRepository.save(newCart);
            });
    }

    private CartResponse convertToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotalAmount(cart.getTotalAmount());
        response.setItems(cart.getItems().stream()
            .map(this::convertToCartItemResponse)
            .collect(Collectors.toList()));
        return response;
    }

    private CartItemResponse convertToCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setProductImageUrl(item.getProduct().getImageUrl());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());
        return response;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
} 