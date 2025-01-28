package com.example.shop_order.service;

import com.example.shop_order.DTOs.OrderSession;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.enums.PaymentType;
import com.example.shop_order.exceptions.InsufficientPointsException;
import com.example.shop_order.exceptions.InvalidPromoCodeException;
import com.example.shop_order.repository.CustomerRepository;
import com.example.shop_order.repository.OrderRepository;
import com.example.shop_order.repository.PromoCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Service for order entity
 */
@Service
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PromoCodeRepository promoCodeRepository;
    @Autowired
    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        PromoCodeRepository promoCodeRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.promoCodeRepository = promoCodeRepository;
    }

    /**
     * Checks if the order contains large items
     *
     * @param order order to check
     * @return true if the order contains large items, false otherwise
     */
    public boolean hasLargeItems(Order order) {
        return order.getItems().stream().anyMatch(item -> item.getProduct().getIsLargeItem());
    }

    /**
     * Applies a promo code to the order
     *
     * @param order        order to apply the promo code to
     * @param orderSession order session
     * @param code         promo code
     */
    public void applyPromoCode(Order order, OrderSession orderSession, String code) {
        if (order.getPromoCode() != null) {
            throw new InvalidPromoCodeException("W tym zamówieniu użyto już kodu rabatowego");
        }

        if (orderSession.getUsedPromoCodes().contains(code)) {
            throw new InvalidPromoCodeException("Użyłeś już tego kodu rabatowego");
        }

        PromoCode promoCode = (PromoCode) promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new InvalidPromoCodeException("Nieprawidłowy kod promocyjny"));

        order.setPromoCode(promoCode.getCode());
        order.setDiscount(promoCode.getDiscountPercentage() / 100 * order.getSubtotal());
        order.calculateTotal();

        orderSession.getUsedPromoCodes().add(code);
    }

    /**
     * Applies loyalty points to the order
     *
     * @param order  order to apply the loyalty points to
     * @param points loyalty points
     */
    public void applyLoyaltyPoints(Order order, Integer points) {
        System.out.println("Applying loyalty points: " + points);
        Customer customer = order.getCustomer();
        if (customer.getLoyaltyPoints() < points) {
            throw new InsufficientPointsException("Not enough loyalty points");
        }

        Double pointsDiscount = calculatePointsDiscount(points);
        order.setUsedLoyaltyPoints(points);
        order.setDiscount(order.getDiscount() + pointsDiscount);

        customer.setLoyaltyPoints(customer.getLoyaltyPoints() - points);

        System.out.println("aftr: " + customer.getLoyaltyPoints());
        customerRepository.save(customer);
        orderRepository.save(order);
    }

    /**
     * Calculates the discount based on the loyalty points
     *
     * @param points loyalty points
     * @return discount
     */
    private Double calculatePointsDiscount(Integer points) {
        return points * 0.1;
    }

    /**
     * Processes the payment for the order
     *
     * @param order       order to process the payment for
     * @param paymentType payment type
     */
    public void processPayment(Order order, PaymentType paymentType) {
    }

    /**
     * Completes the order
     *
     * @param order order to complete
     */
    public void completeOrder(Order order) {
    }

}
