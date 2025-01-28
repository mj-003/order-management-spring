package com.example.shop_order.model;

import lombok.Data;
import javax.persistence.Embeddable;

/**
 * Address class
 */
@Embeddable
@Data
public class Address {
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String postalCode;
    private String city;
    private String phone;
}