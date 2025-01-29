package com.example.shop_order.unitTests.controller;

import com.example.shop_order.DTOs.*;
import com.example.shop_order.controller.OrderController;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.enums.PaymentType;
import com.example.shop_order.exceptions.InsufficientPointsException;
import com.example.shop_order.exceptions.InvalidPromoCodeException;
import com.example.shop_order.service.CustomerService;
import com.example.shop_order.service.DataInitializationService;
import com.example.shop_order.service.DeliveryService;
import com.example.shop_order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;
    @Mock
    private DeliveryService deliveryService;
    @Mock
    private CustomerService customerService;
    @Mock
    private DataInitializationService dataInitializationService;
    @Mock
    private Model model;

    @InjectMocks
    private OrderController orderController;

    private OrderSession orderSession;
    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        orderSession = new OrderSession();
        order = new Order();
        customer = new Customer();
        orderSession.setOrder(order);
        order.setCustomer(customer);
    }

    @Test
    void simulateOrder_SmallScenario_ShouldReturnRedirectToType() {
        // Arrange
        Customer testCustomer = new Customer();
        Order testOrder = new Order();
        when(customerService.findByEmail("jan.kowalski@example.com"))
                .thenReturn(Optional.of(testCustomer));
        when(dataInitializationService.createSmallItemsOrder(testCustomer))
                .thenReturn(testOrder);

        // Act
        String result = orderController.simulateOrder("small", orderSession);

        // Assert
        assertEquals("redirect:/order/type", result);
        assertEquals(CustomerType.INDIVIDUAL, orderSession.getCustomerType());
        assertNotNull(orderSession.getOrder());
    }

    @Test
    void processCustomerType_ShouldSetTypeAndRedirect() {
        // Act
        String result = orderController.processCustomerType(CustomerType.COMPANY, orderSession);

        // Assert
        assertEquals("redirect:/order/details", result);
        assertEquals(CustomerType.COMPANY, orderSession.getCustomerType());
        assertEquals(CustomerType.COMPANY, orderSession.getOrder().getCustomer().getType());
    }

    @Test
    void processCustomerForm_CompanyCustomer_ShouldCreateCustomerAndRedirect() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("companyName", "Test Company");
        request.setParameter("nip", "1234567890");
        request.setParameter("regon", "123456789");
        request.setParameter("email", "company@test.com");
        request.setParameter("phoneNumber", "123456789");

        orderSession.setCustomerType(CustomerType.COMPANY);
        when(customerService.createCustomer(any(CustomerForm.class), eq(CustomerType.COMPANY)))
                .thenReturn(new Customer());

        // Act
        String result = orderController.processCustomerForm(request, orderSession);

        // Assert
        assertEquals("redirect:/order/delivery", result);
        verify(customerService).createCustomer(any(CompanyCustomerForm.class), eq(CustomerType.COMPANY));
    }

    @Test
    void applyPromoCode_ValidCode_ShouldReturnSuccess() {
        // Arrange
        String promoCode = "VALID10";
        doNothing().when(orderService).applyPromoCode(any(Order.class), any(OrderSession.class), eq(promoCode));

        // Act
        ResponseEntity<?> response = orderController.applyPromoCode(promoCode, orderSession);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(orderService).applyPromoCode(eq(order), eq(orderSession), eq(promoCode));
    }

    @Test
    void applyPromoCode_InvalidCode_ShouldReturnError() {
        // Arrange
        String promoCode = "INVALID";
        doThrow(new InvalidPromoCodeException("Invalid code"))
                .when(orderService).applyPromoCode(any(Order.class), any(OrderSession.class), eq(promoCode));

        // Act
        ResponseEntity<?> response = orderController.applyPromoCode(promoCode, orderSession);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Invalid code", body.get("error"));
    }

    @Test
    void applyLoyaltyPoints_ValidPoints_ShouldReturnSuccess() {
        // Arrange
        Integer points = 100;
        customer.setLoyaltyPoints(200);
        order.setSubtotal(1000.0);
        order.setDeliveryCost(50.0);
        order.setDiscount(0.0);
        doNothing().when(orderService).applyLoyaltyPoints(any(Order.class), eq(points));

        // Act
        ResponseEntity<?> response = orderController.applyLoyaltyPoints(points, orderSession);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(orderService).applyLoyaltyPoints(eq(order), eq(points));
    }

    @Test
    void applyLoyaltyPoints_InsufficientPoints_ShouldReturnError() {
        // Arrange
        Integer points = 500;
        doThrow(new InsufficientPointsException("Insufficient points"))
                .when(orderService).applyLoyaltyPoints(any(Order.class), eq(points));

        // Act
        ResponseEntity<?> response = orderController.applyLoyaltyPoints(points, orderSession);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Insufficient points", body.get("error"));
    }

    @Test
    void showDeliveryOptions_ShouldPopulateModelAndReturnView() {
        // Arrange
        List<DeliveryType> deliveryTypes = Arrays.asList(DeliveryType.values());
        List<LocalDateTime> availableDates = Arrays.asList(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        when(orderService.hasLargeItems(any(Order.class))).thenReturn(false);
        when(deliveryService.getAvailableDeliveryOptions(false)).thenReturn(deliveryTypes);
        when(deliveryService.getAvailableDeliveryDates()).thenReturn(availableDates);

        // Act
        String result = orderController.showDeliveryOptions(orderSession, model);

        // Assert
        assertEquals("order/delivery", result);
        verify(model).addAttribute(eq("deliveryOptions"), eq(deliveryTypes));
        verify(model).addAttribute(eq("availableDates"), eq(availableDates));
        verify(model).addAttribute(eq("hasLargeItems"), eq(false));
    }

    @Test
    void processPayment_ShouldSetPaymentTypeAndRedirect() {
        // Act
        String result = orderController.processPayment(PaymentType.BLIK, orderSession);

        // Assert
        assertEquals("redirect:/order/summary", result);
        verify(orderService).processPayment(eq(order), eq(PaymentType.BLIK));
    }

    @Test
    void handlePaymentSuccess_ShouldCompleteOrderAndReturnSuccessView() {
        // Act
        String result = orderController.handlePaymentSuccess(orderSession, model);

        // Assert
        assertEquals("order/success", result);
        verify(orderService).completeOrder(eq(order));
        verify(model).addAttribute(eq("order"), eq(order));
    }
}