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

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createOrUpdateCustomer(CustomerForm form) {
        Customer customer = new Customer();

        // Ustawiamy podstawowe dane
        customer.setEmail(form.getEmail());
        customer.setPhoneNumber(form.getPhoneNumber());
//        customer.setAddress(form.getAddress());

        // W zależności od typu formularza ustawiamy odpowiednie pola
        if (form instanceof CompanyCustomerForm companyForm) {
            customer.setCompanyName(companyForm.getCompanyName());
            customer.setNip(companyForm.getNip());
        } else if (form instanceof IndividualCustomerForm individualForm) {
            customer.setFirstName(individualForm.getFirstName());
            customer.setLastName(individualForm.getLastName());
        }

        return customer;
    }

    private void updateCompanyCustomer(Customer customer, CompanyCustomerForm form) {
        customer.setType(CustomerType.COMPANY);
        customer.setCompanyName(form.getCompanyName());
        customer.setNip(form.getNip());
        customer.setEmail(form.getEmail());
        customer.setPhoneNumber(form.getPhoneNumber());
    }

    private void updateIndividualCustomer(Customer customer, IndividualCustomerForm form) {
        customer.setType(CustomerType.INDIVIDUAL);
        customer.setFirstName(form.getFirstName());
        customer.setLastName(form.getLastName());
        customer.setEmail(form.getEmail());
        customer.setPhoneNumber(form.getPhoneNumber());
    }

    public Customer createCustomer(CustomerForm form, CustomerType customerType) {
        Customer customer = new Customer();
        customer.setType(customerType);
        customer.setEmail(form.getEmail());
        customer.setPhoneNumber(form.getPhoneNumber());
        customer.setFirstName(form.getFirstName());
        customer.setLastName(form.getLastName());
        return customer;
    }
}
