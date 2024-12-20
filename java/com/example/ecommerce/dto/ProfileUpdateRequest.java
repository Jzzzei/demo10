package com.example.ecommerce.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private AddressRequest defaultShippingAddress;
    private AddressRequest defaultBillingAddress;
} 