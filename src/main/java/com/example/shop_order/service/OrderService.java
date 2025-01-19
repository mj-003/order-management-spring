package com.example.shop_order.service;

import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.enums.OrderStatus;
import com.example.shop_order.enums.PaymentType;
import com.example.shop_order.exceptions.InvalidPromoCodeException;
import com.example.shop_order.exceptions.InsufficientPointsException;
import com.example.shop_order.repository.CustomerRepository;
import com.example.shop_order.repository.OrderRepository;
import com.example.shop_order.repository.PromoCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final EmailService emailService;
    private final DocumentService documentService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        PromoCodeRepository promoCodeRepository,
                        EmailService emailService,
                        DocumentService documentService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.emailService = emailService;
        this.documentService = documentService;
    }

    public Order createOrder(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.DRAFT);
        return orderRepository.save(order);
    }

    public boolean hasLargeItems(Order order) {
        return order.getItems().stream().anyMatch(item -> item.getProduct().getIsLargeItem());
    }

    public void applyPromoCode(Order order, String promoCode) {
        PromoCode code = promoCodeRepository
                .findValidPromoCode(promoCode, LocalDateTime.now())
                .orElseThrow(() -> new InvalidPromoCodeException("Invalid or expired promo code"));

        Double discount = calculateDiscount(order.getSubtotal(), code.getDiscountPercentage());
        order.setPromoCode(promoCode);
        order.setDiscount(discount);
        orderRepository.save(order);
    }

    private Double calculateDiscount(Double subtotal, Double discountPercentage) {
        return subtotal * discountPercentage / 100;
    }

    public void applyLoyaltyPoints(Order order, Integer points) {
        Customer customer = order.getCustomer();
        if (customer.getLoyaltyPoints() < points) {
            throw new InsufficientPointsException("Not enough loyalty points");
        }

        Double pointsDiscount = calculatePointsDiscount(points);
        order.setUsedLoyaltyPoints(points);
        order.setDiscount(order.getDiscount() + pointsDiscount);

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - points);
        customerRepository.save(customer);
        orderRepository.save(order);
    }

    private Double calculatePointsDiscount(Integer points) {
        return points * 0.1;
    }

    public void processPayment(Order order, PaymentType paymentType) {
        // Implementacja procesu płatności
//        order.setPaymentType(paymentType);
//        order.setStatus(OrderStatus.PENDING_PAYMENT);
//        orderRepository.save(order);
    }

    public void completeOrder(Order order) {
//        order.setStatus(OrderStatus.COMPLETED);
//        Order savedOrder = orderRepository.save(order);order

        // Generowanie dokumentu
//        Document document = documentService.generateDocument(savedOrder);

        // Wysyłanie potwierdzenia
//        emailService.sendOrderConfirmation(savedOrder, document);
    }

    public void updateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        // Na tym etapie po prostu aktualizujemy obiekt w pamięci
        // Faktyczny zapis do bazy możemy zrobić później, np. przy finalizacji zamówienia
        log.debug("Order updated: {}", order);
    }
}
