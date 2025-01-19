package com.example.shop_order.enums;

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
