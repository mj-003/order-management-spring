package com.example.shop_order.DTOs;

import com.example.shop_order.model.Address;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * DTO class for customer form
 */
@Data
public abstract class CustomerForm {
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotBlank(message = "Numer telefonu jest wymagany")
    private String phoneNumber;

    @Valid
    private Address address;

    private Integer loyaltyPoints;

}