package com.example.shop_order.service;

import com.example.shop_order.DTOs.CompanyCustomerForm;
import com.example.shop_order.DTOs.CustomerForm;
import com.example.shop_order.DTOs.IndividualCustomerForm;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void createFormFromCustomer_WhenCompanyCustomer_ShouldReturnCompanyForm() {
        // Given
        Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setEmail("firma@example.com");
        customer.setPhone("123456789");
        customer.setCompanyName("Test Sp. z o.o.");
        customer.setNip("1234567890");
        customer.setRegon("123456789");
        customer.setLoyaltyPoints(100);

        // When
        CustomerForm form = customerService.createFormFromCustomer(customer);

        // Then
        assertTrue(form instanceof CompanyCustomerForm);
        CompanyCustomerForm companyForm = (CompanyCustomerForm) form;
        assertEquals("firma@example.com", companyForm.getEmail());
        assertEquals("123456789", companyForm.getPhoneNumber());
        assertEquals("Test Sp. z o.o.", companyForm.getCompanyName());
        assertEquals("1234567890", companyForm.getNip());
        assertEquals("123456789", companyForm.getRegon());
        assertEquals(100, companyForm.getLoyaltyPoints());
    }

    @Test
    void createFormFromCustomer_WhenIndividualCustomer_ShouldReturnIndividualForm() {
        // Given
        Customer customer = new Customer();
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setEmail("jan@example.com");
        customer.setPhone("987654321");
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");
        customer.setLoyaltyPoints(50);

        // When
        CustomerForm form = customerService.createFormFromCustomer(customer);

        // Then
        assertTrue(form instanceof IndividualCustomerForm);
        IndividualCustomerForm individualForm = (IndividualCustomerForm) form;
        assertEquals("jan@example.com", individualForm.getEmail());
        assertEquals("987654321", individualForm.getPhoneNumber());
        assertEquals("Jan", individualForm.getFirstName());
        assertEquals("Kowalski", individualForm.getLastName());
        assertEquals(50, individualForm.getLoyaltyPoints());
    }

    @Test
    void createCustomer_WhenCompanyForm_ShouldReturnCompanyCustomer() {
        // Given
        CompanyCustomerForm form = new CompanyCustomerForm();
        form.setEmail("firma@example.com");
        form.setPhoneNumber("123456789");
        form.setCompanyName("Test Sp. z o.o.");
        form.setNip("1234567890");
        form.setRegon("123456789");
        form.setLoyaltyPoints(100);

        // When
        Customer customer = customerService.createCustomer(form, CustomerType.COMPANY);

        // Then
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals("firma@example.com", customer.getEmail());
        assertEquals("123456789", customer.getPhone());
        assertEquals("Test Sp. z o.o.", customer.getCompanyName());
        assertEquals("1234567890", customer.getNip());
        assertEquals("123456789", customer.getRegon());
        assertEquals(100, customer.getLoyaltyPoints());
    }

    @Test
    void findByEmail_WhenCustomerExists_ShouldReturnCustomer() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        when(customerRepository.findByEmail(email)).thenReturn(List.of(customer));

        // When
        Optional<Customer> result = customerService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void findByEmail_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(List.of());

        // When
        Optional<Customer> result = customerService.findByEmail(email);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void save_ShouldCallRepository() {
        // Given
        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        // When
        customerService.save(customer);

        // Then
        verify(customerRepository).save(customer);
    }
}
