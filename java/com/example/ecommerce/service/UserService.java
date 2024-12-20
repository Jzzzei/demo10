package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse updateProfile(ProfileUpdateRequest request);
    Page<OrderResponse> getUserOrders(Pageable pageable);
    UserProfileResponse getUserById(Long id);
    Page<UserProfileResponse> getAllUsers(Pageable pageable);
    void deleteUser(Long id);
    UserProfileResponse updateUserByAdmin(Long id, ProfileUpdateRequest request);
} 