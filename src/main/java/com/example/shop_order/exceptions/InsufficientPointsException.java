package com.example.shop_order.exceptions;

/**
 * Exception thrown when customer has insufficient points to make an order
 */
public class InsufficientPointsException extends RuntimeException {
    public InsufficientPointsException(String message) {
        super(message);
    }
}
