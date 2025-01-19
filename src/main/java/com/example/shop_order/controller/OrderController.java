package com.example.shop_order.controller;

import com.example.shop_order.DTOs.*;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.OrderItem;
import com.example.shop_order.entity.Product;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.enums.PaymentType;
import com.example.shop_order.exceptions.InsufficientPointsException;
import com.example.shop_order.exceptions.InvalidPromoCodeException;
import com.example.shop_order.model.Address;
import com.example.shop_order.service.CustomerService;
import com.example.shop_order.service.DeliveryService;
import com.example.shop_order.service.OrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
@SessionAttributes("orderSession")
@Slf4j
@Data
public class OrderController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final CustomerService customerService;


    @Autowired
    public OrderController(OrderService orderService,
                           DeliveryService deliveryService,
                           CustomerService customerService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
        this.customerService = customerService;
    }

    @ModelAttribute("availableDates")
    public List<LocalDateTime> getAvailableDates() {
        List<LocalDateTime> dates = deliveryService.getAvailableDeliveryDates();
        return dates.stream()
                .map(date -> LocalDateTime.of(
                        date.toLocalDate(),
                        LocalTime.of(12, 0)
                ))
                .collect(Collectors.toList());
    }

    @ModelAttribute("orderSession")
    public OrderSession orderSession() {
        OrderSession orderSession = new OrderSession();
        Order order = new Order();

        // Symulujemy produkty
        Product sofa = new Product();
        sofa.setId(1L);
        sofa.setName("Sofa narożna PORTO");
        sofa.setPrice(2499.99);
        sofa.setLargeItem(false);

        Product table = new Product();
        table.setId(2L);
        table.setName("Stolik kawowy MILANO");
        table.setPrice(399.99);
        table.setLargeItem(false);

        Product lamp = new Product();
        lamp.setId(3L);
        lamp.setName("Lampa podłogowa LED");
        lamp.setPrice(299.99);
        lamp.setLargeItem(false);

        // Tworzymy OrderItems
        List<OrderItem> items = new ArrayList<>();

        OrderItem sofaItem = new OrderItem();
        sofaItem.setProduct(sofa);
        sofaItem.setQuantity(1);
        sofaItem.setPrice(sofa.getPrice());
        sofaItem.setTotalPrice(sofa.getPrice() * sofaItem.getQuantity());
        items.add(sofaItem);

        OrderItem tableItem = new OrderItem();
        tableItem.setProduct(table);
        tableItem.setQuantity(2);
        tableItem.setPrice(table.getPrice());
        tableItem.setTotalPrice(table.getPrice() * tableItem.getQuantity());
        items.add(tableItem);

        OrderItem lampItem = new OrderItem();
        lampItem.setProduct(lamp);
        lampItem.setQuantity(3);
        lampItem.setPrice(lamp.getPrice());
        lampItem.setTotalPrice(lamp.getPrice() * lampItem.getQuantity());
        items.add(lampItem);

        // Ustawiamy podstawowe dane zamówienia
        order.setItems(items);
        order.setSubtotal(items.stream().mapToDouble(OrderItem::getTotalPrice).sum());
        order.setDeliveryCost(0.0);
        order.setDiscount(0.0);
        order.setHomeDelivery(false);

        orderSession.setOrder(order);
        orderSession.setCustomerType(CustomerType.INDIVIDUAL);

        log.debug("Created new OrderSession with items: {}", orderSession);
        return orderSession;
    }

    @GetMapping("/type")
    public String showCustomerTypeSelection(Model model, @ModelAttribute("orderSession") OrderSession orderSession) {
        if (orderSession.getOrder() == null) {
            orderSession.setOrder(new Order());
        }
        model.addAttribute("customerTypes", CustomerType.values());
        return "order/customer-type";
    }

    @PostMapping("/type")
    public String processCustomerType(@RequestParam CustomerType type,
                                      @ModelAttribute("orderSession") OrderSession session) {
        log.debug("Received customer type: {}", type);  // dodaj to
        log.debug("Current session before update: {}", session);  // i to
        session.setCustomerType(type);
        log.debug("Session after update: {}", session);  // i to
        return "redirect:/order/details";
    }

    @GetMapping("/details")
    public String showCustomerForm(@ModelAttribute("orderSession") OrderSession session,
                                   Model model) {
        CustomerForm form = session.getCustomerType() == CustomerType.COMPANY ?
                new CompanyCustomerForm() : new IndividualCustomerForm();
        model.addAttribute("customerForm", form);
        return "order/customer-details";
    }

    @PostMapping("/details")
    public String processCustomerForm(@ModelAttribute("customerForm") CustomerForm form,
                                      BindingResult result,
                                      @ModelAttribute("orderSession") OrderSession session) {
        if (result.hasErrors()) {
            return "order/customer-details";
        }


        Customer customer = customerService.createCustomer(form, session.getCustomerType());
        session.getOrder().setCustomer(customer);
        System.out.println("customer: " + customer);

        return "redirect:/order/delivery";
    }

    @GetMapping("/delivery")
    public String showDeliveryOptions(@ModelAttribute("orderSession") OrderSession session,
                                      Model model) {
        // Sprawdź czy order istnieje
        if (session.getOrder() == null) {
            session.setOrder(new Order());
        }

        // Dodaj formularz dostawy do modelu jeśli nie istnieje
        if (session.getDeliveryForm() == null) {
            session.setDeliveryForm(new DeliveryForm());
        }

        boolean hasLargeItems = orderService.hasLargeItems(session.getOrder());
        List<DeliveryType> options = deliveryService.getAvailableDeliveryOptions(hasLargeItems);
        List<LocalDateTime> availableDates = deliveryService.getAvailableDeliveryDates();

        model.addAttribute("deliveryForm", session.getDeliveryForm());
        model.addAttribute("deliveryOptions", options);
        model.addAttribute("hasLargeItems", hasLargeItems);
        model.addAttribute("availableDates", availableDates);

        return "order/delivery";
    }

    @PostMapping("/delivery")
    public String processDelivery(@ModelAttribute("deliveryForm") DeliveryForm deliveryForm,
                                  @ModelAttribute("orderSession") OrderSession orderSession) {
        Order order = orderSession.getOrder();
        if (order == null) {
            return "redirect:/order/type";
        }

        // Kopiujemy dane z formularza do ordera
        order.setDeliveryAddress(deliveryForm.getDeliveryAddress()); // Zakładając, że Order też używa klasy Address
        order.setDeliveryType(deliveryForm.getDeliveryType());
        order.setHomeDelivery(deliveryForm.isHomeDelivery());
        order.setDeliveryDate(deliveryForm.getDeliveryDate());
        order.setPhone(deliveryForm.getPhone());

        // Zapisujemy punkt odbioru w zależności od typu dostawy
        if (deliveryForm.getDeliveryType() == DeliveryType.INPOST) {
            order.setInpostPoint(deliveryForm.getInpostPoint());
        } else if (deliveryForm.getDeliveryType() == DeliveryType.PICKUP) {
            order.setPickupPoint(deliveryForm.getPickupPoint());
        }

        // Możesz dodać logowanie, żeby sprawdzić czy dane są poprawnie zapisywane
        System.out.println("Delivery Address: " + order.getDeliveryAddress());
        System.out.println("Delivery Type: " + order.getDeliveryType());
        System.out.println("Home Delivery: " + order.isHomeDelivery());

        return "redirect:/order/summary";
    }

    @PostMapping("/apply-promo")
    @ResponseBody
    public ResponseEntity<?> applyPromoCode(@RequestParam String code,
                                            @ModelAttribute("orderSession") OrderSession session) {
        try {
            orderService.applyPromoCode(session.getOrder(), code);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Kod rabatowy został zastosowany"));
        } catch (InvalidPromoCodeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }

    }

    @PostMapping("/apply-points")
    @ResponseBody
    public ResponseEntity<?> applyLoyaltyPoints(@RequestParam Integer points,
                                                @ModelAttribute("orderSession") OrderSession session) {
        try {
            orderService.applyLoyaltyPoints(session.getOrder(), points);
            return ResponseEntity.ok(Map.of(
                    "message", "Punkty zostały naliczone",
                    "remainingPoints", session.getOrder().getCustomer().getLoyaltyPoints()
            ));
        } catch (InsufficientPointsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public String showOrderSummary(@ModelAttribute("orderSession") OrderSession session,
                                   Model model) {
        Order order = session.getOrder();
        if (order == null) {
            return "redirect:/order/type";
        }

        log.debug("address: " + order.getDeliveryAddress());
        System.out.println("address: " + order.getDeliveryAddress());
        System.out.println("order info: " + order);

        if (order.getDeliveryAddress() == null ||
                order.getDeliveryAddress().getStreet() == null ||
                order.getDeliveryAddress().getBuildingNumber() == null ||
                order.getDeliveryAddress().getPostalCode() == null ||
                order.getDeliveryAddress().getCity() == null) {
            return "redirect:/order/delivery";
        }

        model.addAttribute("order", order);
        return "order/summary";
    }

    @PostMapping("/summary")
    public String confirmOrder(@ModelAttribute("orderSession") OrderSession session) {
        Order order = session.getOrder();
        if (order == null) {
            return "redirect:/order/type";
        }

        return "redirect:/order/payment";
    }

    @GetMapping("/payment")
    public String showPaymentOptions(@ModelAttribute("orderSession") OrderSession session,
                                     Model model) {
        // Sprawdź czy zamówienie istnieje
        if (session.getOrder() == null) {
            return "redirect:/order/type";
        }


        Integer availablePoints = 0;
        if (session.getOrder().getCustomer() != null) {
            availablePoints = session.getOrder().getCustomer().getLoyaltyPoints();
        }
        model.addAttribute("availablePoints", availablePoints);

        // Dodaj typy płatności
        model.addAttribute("paymentTypes", PaymentType.values());

        return "order/payment";
    }

    @PostMapping("/payment")
    public String processPayment(@RequestParam PaymentType paymentType,
                                 @ModelAttribute("orderSession") OrderSession session) {
        Order order = session.getOrder();
        orderService.processPayment(order, paymentType);

//        // Symulacja przekierowania do zewnętrznego systemu płatności
//        if (paymentService.initiatePayment(order)) {
//            return "redirect:/order/payment-success";
//        } else {
//            return "redirect:/order/payment-failure";
//        }
        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String handlePaymentSuccess(@ModelAttribute("orderSession") OrderSession session) {
        Order order = session.getOrder();
        orderService.completeOrder(order);
        return "order/success";
    }

    @GetMapping("/payment-failure")
    public String handlePaymentFailure() {
        return "order/failure";
    }
}

