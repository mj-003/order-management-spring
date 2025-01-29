package com.example.shop_order.unitTests.service;

import com.example.shop_order.DTOs.OrderSession;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.exceptions.InsufficientPointsException;
import com.example.shop_order.exceptions.InvalidPromoCodeException;
import com.example.shop_order.repository.CustomerRepository;
import com.example.shop_order.repository.OrderRepository;
import com.example.shop_order.repository.PromoCodeRepository;
import com.example.shop_order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PromoCodeRepository promoCodeRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, customerRepository, promoCodeRepository);
    }

    @Test
    void applyLoyaltyPoints_WhenEnoughPoints_ShouldApplyDiscount() {
        // Given
        Customer customer = new Customer();
        customer.setLoyaltyPoints(100);

        Order order = new Order();
        order.setCustomer(customer);
        order.setSubtotal(100.0);
        order.setDiscount(0.0);

        // When
        orderService.applyLoyaltyPoints(order, 50);

        // Then
        assertEquals(50, customer.getLoyaltyPoints());
        assertEquals(5.0, order.getDiscount());
        verify(customerRepository).save(customer);
        verify(orderRepository).save(order);
    }

    @Test
    void applyLoyaltyPoints_WhenNotEnoughPoints_ShouldThrowException() {
        // Given
        Customer customer = new Customer();
        customer.setLoyaltyPoints(20);

        Order order = new Order();
        order.setCustomer(customer);

        // When & Then
        assertThrows(InsufficientPointsException.class,
                () -> orderService.applyLoyaltyPoints(order, 50));
    }

    @Test
    void applyPromoCode_WhenValidCode_ShouldApplyDiscount() {
        // Given
        Order order = new Order();
        order.setSubtotal(100.0);
        order.setDiscount(0.0);
        order.setDeliveryCost(10.0);  // Dodajemy inicjalizację deliveryCost

        OrderSession orderSession = new OrderSession();

        PromoCode promoCode = new PromoCode();
        promoCode.setCode("VALID10");
        promoCode.setDiscountPercentage(10.0);

        when(promoCodeRepository.findByCode("VALID10"))
                .thenReturn(Optional.of(promoCode));

        // When
        orderService.applyPromoCode(order, orderSession, "VALID10");

        // Then
        assertEquals("VALID10", order.getPromoCode());
        assertEquals(10.0, order.getDiscount());
        assertTrue(orderSession.getUsedPromoCodes().contains("VALID10"));
    }

    @Test
    void applyPromoCode_WhenCodeAlreadyUsed_ShouldThrowException() {
        // Given
        Order order = new Order();
        OrderSession orderSession = new OrderSession();
        orderSession.getUsedPromoCodes().add("USED10");

        // When & Then
        assertThrows(InvalidPromoCodeException.class,
                () -> orderService.applyPromoCode(order, orderSession, "USED10"));
    }
}