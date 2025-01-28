package com.example.shop_order.repository;

import com.example.shop_order.entity.Customer;
import com.example.shop_order.enums.CustomerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByEmail_WhenCustomerExists_ShouldReturnCustomer() {
        // Arrange
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPhoneNumber("123456789");
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setLoyaltyPoints(100);
        entityManager.persist(customer);
        entityManager.flush();

        // Act
        List<Customer> found = customerRepository.findByEmail("test@example.com");

        // Assert
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(found.get(0).getPhoneNumber()).isEqualTo("123456789");
        assertThat(found.get(0).getType()).isEqualTo(CustomerType.INDIVIDUAL);
        assertThat(found.get(0).getLoyaltyPoints()).isEqualTo(100);
    }

    @Test
    void findByEmail_WhenCustomerDoesNotExist_ShouldReturnEmptyList() {
        // Act
        List<Customer> found = customerRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_WhenMultipleCustomersWithSameEmail_ShouldReturnAllMatches() {
        // Arrange
        String email = "duplicate@example.com";

        Customer customer1 = new Customer();
        customer1.setEmail(email);
        customer1.setPhoneNumber("111111111");
        customer1.setType(CustomerType.INDIVIDUAL);

        Customer customer2 = new Customer();
        customer2.setEmail(email);
        customer2.setPhoneNumber("222222222");
        customer2.setType(CustomerType.COMPANY);

        entityManager.persist(customer1);
        entityManager.persist(customer2);
        entityManager.flush();

        // Act
        List<Customer> found = customerRepository.findByEmail(email);

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Customer::getEmail).containsOnly(email);
        assertThat(found).extracting(Customer::getPhoneNumber).containsExactlyInAnyOrder("111111111", "222222222");
    }

    @Test
    void save_ShouldPersistCustomer() {
        // Arrange
        Customer customer = new Customer();
        customer.setEmail("save@example.com");
        customer.setPhoneNumber("999999999");
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setLoyaltyPoints(50);

        // Act
        Customer saved = customerRepository.save(customer);

        // Assert
        Customer found = entityManager.find(Customer.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("save@example.com");
        assertThat(found.getPhoneNumber()).isEqualTo("999999999");
        assertThat(found.getType()).isEqualTo(CustomerType.INDIVIDUAL);
        assertThat(found.getLoyaltyPoints()).isEqualTo(50);
    }

    @Test
    void findByEmail_WithNullEmail_ShouldReturnEmptyList() {
        // Act
        List<Customer> found = customerRepository.findByEmail(null);

        // Assert
        assertThat(found).isEmpty();
    }
}