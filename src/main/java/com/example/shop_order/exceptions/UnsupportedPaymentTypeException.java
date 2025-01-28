package com.example.shop_order.exceptions;

/**
 * Exception thrown when unsupported payment type is provided
 */
public class UnsupportedPaymentTypeException extends RuntimeException {
    public UnsupportedPaymentTypeException() {
        super("Nieobsługiwany typ płatności");
    }
}
