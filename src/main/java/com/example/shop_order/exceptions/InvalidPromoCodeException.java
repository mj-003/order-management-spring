package com.example.shop_order.exceptions;

/**
 * Exception thrown when promo code is invalid
 */
public class InvalidPromoCodeException extends RuntimeException {
    public InvalidPromoCodeException(String message) {
        super(message);
    }
}
