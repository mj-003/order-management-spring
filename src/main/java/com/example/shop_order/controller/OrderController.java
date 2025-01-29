package com.example.shop_order.controller;

import com.example.shop_order.DTOs.*;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
    * Controller for handling order process
 *
 */

@Controller
@RequestMapping("/order")
@SessionAttributes("orderSession")
@Slf4j
@Data
public class OrderController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final CustomerService customerService;
    private final DataInitializationService dataInitializationService;

    /**
     * Controller constructor
     * @param orderService
     * @param deliveryService
     * @param customerService
     * @param dataInitializationService
     */
    @Autowired
    public OrderController(OrderService orderService,
                           DeliveryService deliveryService,
                           CustomerService customerService, DataInitializationService dataInitializationService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
        this.customerService = customerService;
        this.dataInitializationService = dataInitializationService;
    }

    /**
     * Method returning available delivery dates
     * @return list of available delivery dates
     */
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

    /**
     * Method returning new OrderSession
     * @return new OrderSession
     */
    @ModelAttribute("orderSession")
    public OrderSession orderSession() {
        OrderSession orderSession = new OrderSession();
        log.debug("Created new OrderSession with items: {}", orderSession);
        return orderSession;
    }

    /**
     * Method simulating order
     * @param scenario
     * @param orderSession
     * @return redirect to order type
     */
    @GetMapping("/simulate/{scenario}")
    public String simulateOrder(@PathVariable String scenario,
                                @ModelAttribute("orderSession") OrderSession orderSession) {
        Order order;
        Customer customer;

        switch (scenario) {
            case "small" -> {
                // scenariusz 1 - klient indywidualny, małe gabaryty
                customer = customerService.findByEmail("jan.kowalski@example.com")
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                order = dataInitializationService.createSmallItemsOrder(customer);
                orderSession.setCustomerType(CustomerType.INDIVIDUAL);
            }
            case "large" -> {
                // scenariusz 2: klient firmowy, duże gabaryty
                customer = customerService.findByEmail("firma@example.com")
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                order = dataInitializationService.createLargeItemsOrder(customer);
                orderSession.setCustomerType(CustomerType.COMPANY);
            }
            case "testSmall" -> {
                // scenariusz 3: testowy, małe gabaryty
                customer = new Customer();
                customer.setLoyaltyPoints(200);
                order = dataInitializationService.createSmallItemsOrder(customer);
                orderSession.setCustomerType(CustomerType.INDIVIDUAL);
            }
            default -> {
                return "redirect:/order/type";
            }
        }

        orderSession.setOrder(order);

        if (scenario.equals("testSmall")) {
            System.out.println("dupa dupa dupa dupa");
            System.out.println(orderSession.getOrder().getCustomer().getLoyaltyPoints());
            orderSession.getOrder().getCustomer().setLoyaltyPoints(500);
            System.out.println(orderSession.getOrder().getCustomer().getLoyaltyPoints());
        }
        return "redirect:/order/type";
    }

    /**
     * Method showing customer type selection
     * @param model
     * @param orderSession
     * @return customer type selection view
     */
    @GetMapping("/type")
    public String showCustomerTypeSelection(Model model, @ModelAttribute("orderSession") OrderSession orderSession) {
        if (orderSession.getOrder() == null) {
            orderSession.setOrder(new Order());
        }

        model.addAttribute("customerTypes", CustomerType.values());
        return "order/customer-type";
    }


    /**
     * Method processing customer type selection
     * @param type
     * @param session
     * @return redirect to order details
     */
    @PostMapping("/type")
    public String processCustomerType(@RequestParam CustomerType type,
                                      @ModelAttribute("orderSession") OrderSession session) {
        session.setCustomerType(type);
        if (session.getOrder() != null && session.getOrder().getCustomer() != null) {
            session.getOrder().getCustomer().setType(type);
        }

        return "redirect:/order/details";
    }


    /**
     * Method showing customer form (individual or company)
     * @param session
     * @param model
     * @return customer form view
     */
    @GetMapping("/details")
    public String showCustomerForm(@ModelAttribute("orderSession") OrderSession session,
                                   Model model) {
        CustomerForm form;

        if (session.getOrder() != null && session.getOrder().getCustomer() != null) {
            form = customerService.createFormFromCustomer(session.getOrder().getCustomer());
        } else {
            if (session.getCustomerType() == CustomerType.COMPANY) {
                form = new CompanyCustomerForm();
            } else {
                form = new IndividualCustomerForm();
            }
        }

        model.addAttribute("customerForm", form);
        model.addAttribute("formType", form.getClass().getSimpleName());

        return "order/customer-details";
    }


    /**
     * Method processing customer form
     * @param request
     * @param session
     * @return redirect to order delivery
     */
    @PostMapping("/details")
    public String processCustomerForm(HttpServletRequest request,
                                      @ModelAttribute("orderSession") OrderSession session) {
        CustomerForm form;
        Integer currentPoints = null;
        if (session.getOrder() != null && session.getOrder().getCustomer() != null) {
            currentPoints = session.getOrder().getCustomer().getLoyaltyPoints();
        }

        if (session.getCustomerType() == CustomerType.COMPANY) {
            CompanyCustomerForm companyForm = new CompanyCustomerForm();
            companyForm.setCompanyName(request.getParameter("companyName"));
            companyForm.setNip(request.getParameter("nip"));
            companyForm.setRegon(request.getParameter("regon"));
            form = companyForm;
        } else {
            IndividualCustomerForm individualForm = new IndividualCustomerForm();
            individualForm.setFirstName(request.getParameter("firstName"));
            individualForm.setLastName(request.getParameter("lastName"));
            form = individualForm;
        }

        form.setEmail(request.getParameter("email"));
        form.setPhoneNumber(request.getParameter("phoneNumber"));
        customerService.findByEmail(form.getEmail())
                .ifPresent(existingCustomer -> {
                    form.setLoyaltyPoints(existingCustomer.getLoyaltyPoints());
                });
        try {
            Customer customer = customerService.createCustomer(form, session.getCustomerType());
            if (currentPoints != null) {
                customer.setLoyaltyPoints(currentPoints);
            }
            session.getOrder().setCustomer(customer);
            return "redirect:/order/delivery";
        } catch (Exception e) {
            return "redirect:/order/details";
        }
    }


    /**
     * Method showing delivery options
     * @param session
     * @param model
     * @return delivery options view
     */
    @GetMapping("/delivery")
    public String showDeliveryOptions(@ModelAttribute("orderSession") OrderSession session,
                                      Model model) {
        if (session.getOrder() == null) {
            session.setOrder(new Order());
        }

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


    /**
     * Method processing delivery options
     * @param deliveryForm
     * @param orderSession
     * @return redirect to order payment
     */
    @PostMapping("/delivery")
    public String processDelivery(@ModelAttribute("deliveryForm") DeliveryForm deliveryForm,
                                  @ModelAttribute("orderSession") OrderSession orderSession) {
        Order order = orderSession.getOrder();
        if (order == null) {
            return "redirect:/order/type";
        }

        order.setDeliveryAddress(deliveryForm.getDeliveryAddress());
        order.setDeliveryType(deliveryForm.getDeliveryType());
        order.setHomeDelivery(deliveryForm.isHomeDelivery());
        order.setDeliveryDate(deliveryForm.getDeliveryDate());
        order.setPhone(deliveryForm.getPhone());

        // Ustawiamy koszt dostawy w zależności od typu
        double deliveryCost = deliveryService.calculateDeliveryCost(order);
        order.setDeliveryCost(deliveryCost);

        // Zapisujemy punkt odbioru w zależności od typu dostawy
        if (deliveryForm.getDeliveryType() == DeliveryType.INPOST) {
            order.setInpostPoint(deliveryForm.getInpostPoint());
        } else if (deliveryForm.getDeliveryType() == DeliveryType.PICKUP) {
            order.setPickupPoint(deliveryForm.getPickupPoint());
        }

        order.calculateTotal();
        log.debug("Ustawiono dostawę - typ: {}, koszt: {}", order.getDeliveryType(), order.getDeliveryCost());

        return "redirect:/order/payment";
    }


    /**
     * Method applying promo code
     * @param code
     * @param session
     * @return response with information about promo code application
     */
    @PostMapping("/apply-promo")
    @ResponseBody
    public ResponseEntity<?> applyPromoCode(@RequestParam String code,
                                            @ModelAttribute("orderSession") OrderSession session) {
        try {
            orderService.applyPromoCode(session.getOrder(), session, code);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Kod rabatowy został zastosowany"));
        } catch (InvalidPromoCodeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Method applying loyalty points
     * @param points
     * @param session
     * @return response with information about loyalty points application
     */
    @PostMapping("/apply-points")
    @ResponseBody
    public ResponseEntity<?> applyLoyaltyPoints(@RequestParam Integer points,
                                                @ModelAttribute("orderSession") OrderSession session) {
        try {
            orderService.applyLoyaltyPoints(session.getOrder(), points);
            // Po naliczeniu punktów przelicz sumę
            session.getOrder().calculateTotal();

            return ResponseEntity.ok(Map.of(
                    "message", "Punkty zostały naliczone",
                    "remainingPoints", session.getOrder().getCustomer().getLoyaltyPoints(),
                    "total", session.getOrder().getTotal() // dodaj aktualną sumę do odpowiedzi
            ));
        } catch (InsufficientPointsException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Method showing order summary
     * @param session
     * @param model
     * @return order summary view
     */
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

    /**
     * Method confirming order
     * @param session
     * @return redirect to order success
     */
    @PostMapping("/summary")
    public String confirmOrder(@ModelAttribute("orderSession") OrderSession session) {
        Order order = session.getOrder();
        if (order == null) {
            return "redirect:/order/type";
        }

        return "redirect:/order/success";
    }

    /**
     * Method showing payment options
        * @param session
     * @param model
     * @return payment options view
     */
    @GetMapping("/payment")
    public String showPaymentOptions(@ModelAttribute("orderSession") OrderSession session,
                                     Model model) {
        // Sprawdź czy zamówienie istnieje
        if (session.getOrder() == null) {
            return "redirect:/order/type";
        }


        Integer availablePoints = 0;
        if (session.getOrder().getCustomer() != null) {
            System.out.println(session.getOrder().getCustomer());
            availablePoints = session.getOrder().getCustomer().getLoyaltyPoints();
            System.out.println("customer points: " + session.getOrder().getCustomer().getLoyaltyPoints());
            System.out.println("available points: " + availablePoints);
        }
        model.addAttribute("availablePoints", availablePoints);

        // Dodaj typy płatności
        model.addAttribute("paymentTypes", PaymentType.values());

        if (session.getOrder() == null) {
            return "redirect:/order/type";
        }
        return "order/payment";
    }

    /**
     * Method processing payment
     * @param paymentType
     * @param session
     * @return redirect to order summary
     */
    @PostMapping("/payment")
    public String processPayment(@RequestParam PaymentType paymentType,
                                 @ModelAttribute("orderSession") OrderSession session) {
        Order order = session.getOrder();
        orderService.processPayment(order, paymentType);
        return "redirect:/order/summary";
    }

    /**
     * Method showing payment success information
     * @param session
     * @param model
     * @return payment success view
     */
    @GetMapping("/success")
    public String handlePaymentSuccess(
            @ModelAttribute("orderSession") OrderSession session,
            Model model) {

        Order order = session.getOrder();
        orderService.completeOrder(order);
        model.addAttribute("order", order);

        return "order/success";
    }

    /**
     * Method showing payment failure information
     * @return payment failure view
     */
    @GetMapping("/payment-failure")
    public String handlePaymentFailure() {
        return "order/failure";
    }
}

