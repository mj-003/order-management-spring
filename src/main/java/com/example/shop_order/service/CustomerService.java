package com.example.shop_order.service;

import com.example.shop_order.DTOs.CompanyCustomerForm;
import com.example.shop_order.DTOs.CustomerForm;
import com.example.shop_order.DTOs.IndividualCustomerForm;
import com.example.shop_order.entity.Customer;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service for customer entity. It provides methods for creating customer form from customer entity and vice versa.
 */
@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;


    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Creates a form from a customer entity. The form type depends on the customer type.
     *
     * @param customer customer entity
     * @return customer form
     */
    public CustomerForm createFormFromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerForm form;
        if (customer.getType() == CustomerType.COMPANY) {
            CompanyCustomerForm companyForm = new CompanyCustomerForm();
            companyForm.setCompanyName(customer.getCompanyName());
            companyForm.setNip(customer.getNip());
            companyForm.setRegon(customer.getRegon());
            form = companyForm;
        } else {
            IndividualCustomerForm individualForm = new IndividualCustomerForm();
            individualForm.setFirstName(customer.getFirstName());
            individualForm.setLastName(customer.getLastName());
            form = individualForm;
        }

        // Wspólne pola
        form.setEmail(customer.getEmail());
        form.setPhoneNumber(customer.getPhone());
        form.setLoyaltyPoints(customer.getLoyaltyPoints());

        System.out.println("Final form type: " + form.getClass().getSimpleName());
        return form;
    }

    /**
     * Creates a customer entity from a customer form. The entity type depends on the customer type.
     *
     * @param form customer form
     * @param type customer type
     * @return customer entity
     */
    public Customer createCustomer(CustomerForm form, CustomerType type) {
        Customer customer = new Customer();

        // Wspólne pola
        customer.setEmail(form.getEmail());
        customer.setPhone(form.getPhoneNumber());
        customer.setLoyaltyPoints(form.getLoyaltyPoints());
        customer.setType(type);

        // Specyficzne pola w zależności od typu
        if (form instanceof CompanyCustomerForm companyForm) {
            customer.setCompanyName(companyForm.getCompanyName());
            customer.setNip(companyForm.getNip());
            customer.setRegon(companyForm.getRegon());
        } else if (form instanceof IndividualCustomerForm individualForm) {
            customer.setFirstName(individualForm.getFirstName());
            customer.setLastName(individualForm.getLastName());
        }

        return customer;
    }

    /**
     * Finds a customer by email.
     *
     * @param email customer email
     * @return customer entity
     */
    public Optional<Customer> findByEmail(String email) {
        List<Customer> customers = customerRepository.findByEmail(email);
        if (customers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(customers.get(0));
    }

    /**
     * Saves a customer entity.
     *
     * @param individual customer entity
     * @return saved customer entity
     */
    public Customer save(Customer individual) {
        customerRepository.save(individual);
        return individual;
    }

}
