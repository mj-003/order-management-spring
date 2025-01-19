package com.example.shop_order.exceptions;

public class UnsupportedPaymentTypeException extends RuntimeException {
    public UnsupportedPaymentTypeException() {
        super("Nieobsługiwany typ płatności");
    }
}
