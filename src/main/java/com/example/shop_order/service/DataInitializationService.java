package com.example.shop_order.service;

import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.OrderItem;
import com.example.shop_order.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for initializing data in the application. It creates products, customers and orders.
 */
@Service
@Transactional
@Slf4j
public class DataInitializationService {
    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;
    private static boolean isInitialized = false;

    @Autowired
    public DataInitializationService(CustomerService customerService,
                                     ProductService productService,
                                     OrderService orderService) {
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
    }

    /**
     * Initializes data in the application. It creates products and customers.
     */
    @PostConstruct
    @Transactional
    public void initializeData() {
        if (isInitialized) {
            log.info("Dane już zostały zainicjalizowane");
            return;
        }

        try {
            log.info("Rozpoczynam inicjalizację danych...");

            // Inicjalizacja produktów
            initializeProducts();

            // Inicjalizacja klientów
            initializeCustomers();

            isInitialized = true;
            log.info("Inicjalizacja danych zakończona pomyślnie");
        } catch (Exception e) {
            log.error("Błąd podczas inicjalizacji danych: " + e.getMessage(), e);
            throw new RuntimeException("Nie udało się zainicjalizować danych", e);
        }
    }

    /**
     * Creates a small items order for a customer.
     *
     * @return order
     */
    private void initializeProducts() {
        log.info("Inicjalizacja produktów...");

        // Produkty małogabarytowe
        createAndSaveProduct("Lampa stołowa LED", 199.99, false);
        createAndSaveProduct("Krzesło biurowe BASIC", 299.99, false);
        createAndSaveProduct("Stolik kawowy MILANO", 399.99, false);

        // Produkty wielkogabarytowe
        createAndSaveProduct("Sofa narożna PORTO", 2499.99, true);
        createAndSaveProduct("Szafa przesuwna XXL", 1899.99, true);

        log.info("Inicjalizacja produktów zakończona");
    }

    /**
     * Creates and saves a product if it does not exist.
     *
     * @param name    product name
     * @param price   product price
     * @param isLarge true if the product is large, false otherwise
     */
    private void createAndSaveProduct(String name, double price, boolean isLarge) {
        if (productService.findByName(name) == null) {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setLargeItem(isLarge);
            Product saved = productService.save(product);
            log.info("Zapisano produkt: {} (ID: {})", saved.getName(), saved.getId());
        } else {
            log.info("Produkt {} już istnieje, pomijam", name);
        }
    }

    /**
     * Initializes customers in the application.
     */
    private void initializeCustomers() {
        log.info("Inicjalizacja klientów...");

        // Klient indywidualny
        if (customerService.findByEmail("jan.kowalski@example.com").isEmpty()) {
            Customer individual = new Customer();
            individual.setEmail("jan.kowalski@example.com");
            individual.setFirstName("Jan");
            individual.setLastName("Kowalski");
            individual.setPhone("123456789");
            individual.setLoyaltyPoints(0);
            Customer saved = customerService.save(individual);
            log.info("Zapisano klienta indywidualnego: {}", saved.getEmail());
        }

        // Klient firmowy
        if (customerService.findByEmail("firma@example.com").isEmpty()) {
            Customer company = new Customer();
            company.setNip("1234567890");
            company.setEmail("firma@example.com");
            company.setCompanyName("Przykładowa Firma Sp. z o.o.");
            company.setNip("1234567890");
            company.setPhone("987654321");
            company.setLoyaltyPoints(500);
            System.out.println(company.getLoyaltyPoints());
            Customer saved = customerService.save(company);
            log.info("klient: {}", saved);
            log.info("pkt: {}", saved.getLoyaltyPoints());
            log.info("Zapisano klienta firmowego: {}", saved.getEmail());
        }

        // prywatny klient do testow
//        if (customerService.findByEmail("test@test.pl").isEmpty()) {
//            Customer testCustomer = new Customer();
//            Customer saved = customerService.save(testCustomer);
//        }

        log.info("Inicjalizacja klientów zakończona");
    }

    /**
     * Creates a small items order for a customer.
     *
     * @param customer customer
     * @return order
     */
    public Order createSmallItemsOrder(Customer customer) {
        log.info("Tworzenie zamówienia małogabarytowego dla klienta: {}", customer.getEmail());

        Order order = new Order();
        order.setCustomer(customer);
        List<OrderItem> items = new ArrayList<>();

        // Pobieranie produktów
        Product lamp = productService.findByName("Lampa stołowa LED");
        if (lamp == null) {
            String error = "Nie znaleziono produktu: Lampa stołowa LED";
            log.error(error);
            throw new RuntimeException(error);
        }

        Product chair = productService.findByName("Krzesło biurowe BASIC");
        if (chair == null) {
            String error = "Nie znaleziono produktu: Krzesło biurowe BASIC";
            log.error(error);
            throw new RuntimeException(error);
        }

        // Tworzenie pozycji zamówienia
        OrderItem lampItem = createOrderItem(lamp, 2, order);
        OrderItem chairItem = createOrderItem(chair, 1, order);

        items.add(lampItem);
        items.add(chairItem);

        order.setItems(items);
        order.setSubtotal(calculateSubtotal(items));

        log.info("Utworzono zamówienie małogabarytowe, liczba pozycji: {}", items.size());
        return order;
    }

    /**
     * Creates a large items order for a customer.
     *
     * @param customer customer
     * @return order
     */
    public Order createLargeItemsOrder(Customer customer) {
        log.info("Tworzenie zamówienia wielkogabarytowego dla klienta: {}", customer.getEmail());

        Order order = new Order();
        order.setCustomer(customer);
        List<OrderItem> items = new ArrayList<>();

        // Pobieranie produktów
        Product sofa = productService.findByName("Sofa narożna PORTO");
        if (sofa == null) {
            String error = "Nie znaleziono produktu: Sofa narożna PORTO";
            log.error(error);
            throw new RuntimeException(error);
        }

        Product wardrobe = productService.findByName("Szafa przesuwna XXL");
        if (wardrobe == null) {
            String error = "Nie znaleziono produktu: Szafa przesuwna XXL";
            log.error(error);
            throw new RuntimeException(error);
        }

        // Tworzenie pozycji zamówienia
        OrderItem sofaItem = createOrderItem(sofa, 1, order);
        OrderItem wardrobeItem = createOrderItem(wardrobe, 1, order);

        items.add(sofaItem);
        items.add(wardrobeItem);

        order.setItems(items);
        order.setSubtotal(calculateSubtotal(items));

        log.info("Utworzono zamówienie wielkogabarytowe, liczba pozycji: {}", items.size());
        return order;
    }

    /**
     * Creates an order item for a product.
     *
     * @param product  product
     * @param quantity quantity
     * @param order    order
     * @return order item
     */
    private OrderItem createOrderItem(Product product, int quantity, Order order) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        item.setTotalPrice(product.getPrice() * quantity);
        item.setOrder(order);
        return item;
    }

    /**
     * Calculates the subtotal for an order.
     *
     * @param items order items
     * @return subtotal
     */
    private double calculateSubtotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    public void resetInitialization() {
        isInitialized = false;
        log.info("Reset stanu inicjalizacji");
    }
}