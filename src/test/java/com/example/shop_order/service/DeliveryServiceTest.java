package com.example.shop_order.service;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.DeliveryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService();
    }

    @Test
    void getAvailableDeliveryOptions_WhenNoLargeItems_ShouldReturnAllOptions() {
        // Given
        boolean hasLargeItems = false;

        // When
        List<DeliveryType> options = deliveryService.getAvailableDeliveryOptions(hasLargeItems);

        // Then
        assertEquals(3, options.size());
        assertTrue(options.contains(DeliveryType.COURIER));
        assertTrue(options.contains(DeliveryType.INPOST));
        assertTrue(options.contains(DeliveryType.PICKUP));
    }

    @Test
    void getAvailableDeliveryOptions_WhenHasLargeItems_ShouldNotIncludeInpost() {
        // Given
        boolean hasLargeItems = true;

        // When
        List<DeliveryType> options = deliveryService.getAvailableDeliveryOptions(hasLargeItems);

        // Then
        assertEquals(2, options.size());
        assertTrue(options.contains(DeliveryType.COURIER));
        assertTrue(options.contains(DeliveryType.PICKUP));
        assertFalse(options.contains(DeliveryType.INPOST));
    }

    @Test
    void getAvailableDeliveryDates_ShouldReturnNext7Days() {
        // When
        List<LocalDateTime> dates = deliveryService.getAvailableDeliveryDates();

        // Then
        assertEquals(7, dates.size());

        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < dates.size(); i++) {
            LocalDateTime date = dates.get(i);
            assertEquals(now.plusDays(i + 1).getDayOfYear(), date.getDayOfYear());
            assertEquals(12, date.getHour());
            assertEquals(0, date.getMinute());
        }
    }

    @Test
    void calculateDeliveryCost_WhenCourier_ShouldReturn20() {
        // Given
        Order order = new Order();
        order.setDeliveryType(DeliveryType.COURIER);

        // When
        Double cost = deliveryService.calculateDeliveryCost(order);

        // Then
        assertEquals(20.0, cost);
    }

    @Test
    void calculateDeliveryCost_WhenInpost_ShouldReturn15() {
        // Given
        Order order = new Order();
        order.setDeliveryType(DeliveryType.INPOST);

        // When
        Double cost = deliveryService.calculateDeliveryCost(order);

        // Then
        assertEquals(15.0, cost);
    }

    @Test
    void calculateDeliveryCost_WhenPickup_ShouldReturn0() {
        // Given
        Order order = new Order();
        order.setDeliveryType(DeliveryType.PICKUP);

        // When
        Double cost = deliveryService.calculateDeliveryCost(order);

        // Then
        assertEquals(0.0, cost);
    }

    @Test
    void calculateDeliveryCost_WhenLargeCourier_ShouldReturn259() {
        // Given
        Order order = new Order();
        order.setDeliveryType(DeliveryType.LARGE_COURIER);

        // When
        Double cost = deliveryService.calculateDeliveryCost(order);

        // Then
        assertEquals(259.0, cost);
    }
}