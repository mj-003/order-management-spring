package com.example.shop_order.service;

import com.example.shop_order.entity.Order;
import com.example.shop_order.exceptions.UnsupportedPaymentTypeException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private static final java.util.UUID UUID = null;

    public boolean initiatePayment(Order order) {
        // Symulacja integracji z systemem płatności
        switch (order.getPaymentType()) {
            case BLIK -> {
                return processBlikPayment(order);
            }
            case CARD -> {
                return processCardPayment(order);
            }
            case TRANSFER -> {
                return processTransferPayment(order);
            }
            case GOOGLE_PAY -> {
                return processGooglePayPayment(order);
            }
            default -> throw new UnsupportedPaymentTypeException();
        }
    }

    private boolean processCardPayment(Order order) {
        // Symulacja procesu płatności kartą
        order.setPaymentTransactionId("CARD_" + UUID.randomUUID().toString());
        return true;
    }

    private boolean processTransferPayment(Order order) {
        // Symulacja procesu płatności przelewem
        order.setPaymentTransactionId("TRANSFER_" + UUID.randomUUID().toString());
        return true;
    }

    private boolean processGooglePayPayment(Order order) {
        // Symulacja procesu płatności Google Pay
        order.setPaymentTransactionId("GOOGLE_PAY_" + UUID.randomUUID().toString());
        return true;
    }


    private boolean processBlikPayment(Order order) {
        // Symulacja procesu płatności BLIK
        order.setPaymentTransactionId("BLIK_" + UUID.randomUUID().toString());
        return true;
    }

    // Podobne metody dla innych typów płatności...
}
