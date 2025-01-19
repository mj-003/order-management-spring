package com.example.shop_order.DTOs;

import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.model.Address;
import com.sun.istack.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Data
public class DeliveryForm {
    @NotNull
    private DeliveryType deliveryType = DeliveryType.COURIER;

    private Boolean homeDelivery = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deliveryDate;

    @Valid
    private Address deliveryAddress;

    // Nowe pola dla paczkomatu i punktu odbioru
    private String inpostPoint;
    private String pickupPoint;

    // Pole na numer telefonu (można dodać do głównego formularza zamiast do Address)
    private String phone;

    public Boolean isHomeDelivery() {
        return homeDelivery;
    }


}