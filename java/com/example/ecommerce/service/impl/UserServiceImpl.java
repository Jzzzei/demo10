package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        return convertToUserProfileResponse(getCurrentUser());
    }

    @Override
    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (request.getDefaultShippingAddress() != null) {
            Address shippingAddress = user.getDefaultShippingAddress();
            if (shippingAddress == null) {
                shippingAddress = new Address();
                shippingAddress.setUser(user);
                user.setDefaultShippingAddress(shippingAddress);
            }
            updateAddress(shippingAddress, request.getDefaultShippingAddress());
            addressRepository.save(shippingAddress);
        }

        if (request.getDefaultBillingAddress() != null) {
            Address billingAddress = user.getDefaultBillingAddress();
            if (billingAddress == null) {
                billingAddress = new Address();
                billingAddress.setUser(user);
                billingAddress = addressRepository.save(billingAddress);
                user.setDefaultBillingAddress(billingAddress);
            }
            updateAddress(billingAddress, request.getDefaultBillingAddress());
            addressRepository.save(billingAddress);
        }

        user = userRepository.save(user);
        return convertToUserProfileResponse(user);
    }

    @Override
    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        User user = getCurrentUser();
        return orderRepository.findByUser(user, pageable)
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    // 设置订单响应的属性
                    return response;
                });
    }

    @Override
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserProfileResponse(user);
    }

    @Override
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToUserProfileResponse);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserProfileResponse updateUserByAdmin(Long id, ProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ���新用户信息
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // 更新地址信息
        if (request.getDefaultShippingAddress() != null) {
            Address shippingAddress = user.getDefaultShippingAddress();
            if (shippingAddress == null) {
                shippingAddress = new Address();
                shippingAddress.setUser(user);
                user.setDefaultShippingAddress(shippingAddress);
            }
            updateAddress(shippingAddress, request.getDefaultShippingAddress());
        }

        if (request.getDefaultBillingAddress() != null) {
            Address billingAddress = user.getDefaultBillingAddress();
            if (billingAddress == null) {
                billingAddress = new Address();
                billingAddress.setUser(user);
                user.setDefaultBillingAddress(billingAddress);
            }
            updateAddress(billingAddress, request.getDefaultBillingAddress());
        }

        user = userRepository.save(user);
        return convertToUserProfileResponse(user);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void updateAddress(Address address, AddressRequest request) {
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
        address.setPhone(request.getPhone());
    }

    private AddressResponse convertToAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setZipCode(address.getZipCode());
        response.setPhone(address.getPhone());
        return response;
    }

    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setDefaultShippingAddress(convertToAddressResponse(user.getDefaultShippingAddress()));
        response.setDefaultBillingAddress(convertToAddressResponse(user.getDefaultBillingAddress()));
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        return response;
    }
} 