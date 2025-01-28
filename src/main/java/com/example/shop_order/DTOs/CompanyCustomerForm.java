package com.example.shop_order.DTOs;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * DTO class for company customer form
 */
@Data
public class CompanyCustomerForm extends CustomerForm {
    @NotBlank(message = "Nazwa firmy jest wymagana")
    private String companyName;

    @NotBlank(message = "NIP jest wymagany")
    private String nip;

    private String regon;
}