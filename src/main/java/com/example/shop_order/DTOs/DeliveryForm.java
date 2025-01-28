package com.example.shop_order.DTOs;

import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.model.Address;
import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * DTO class for delivery form
 */
@Data
public class DeliveryForm {
    @NotNull
    private DeliveryType deliveryType = DeliveryType.COURIER;

    private Boolean homeDelivery = false;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deliveryDate;

    @Valid
    private Address deliveryAddress;

    private String inpostPoint;
    private String pickupPoint;

    private String phone;

    public Boolean isHomeDelivery() {
        return homeDelivery;
    }
}