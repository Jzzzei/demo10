package com.example.ecommerce.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressResponse defaultShippingAddress;
    private AddressResponse defaultBillingAddress;
    private List<String> roles;
} 