package com.example.shop_order.enums;

/**
 * Enum for delivery type
 */
public enum DeliveryType {
    COURIER("Kurier"),
    LARGE_COURIER("Kurier (duże gabaryty)"),
    INPOST("Paczkomat InPost"),
    PICKUP("Odbiór osobisty");
    private final String displayName;

    DeliveryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
