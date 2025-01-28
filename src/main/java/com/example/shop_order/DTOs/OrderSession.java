package com.example.shop_order.DTOs;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.enums.PaymentType;
import lombok.Data;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO class for order session. This class is used to store order session data in session scope.
 */
@Data
public class OrderSession implements Serializable {
    private CustomerType customerType;
    private Order order;
    private CustomerForm customerForm;
    private DeliveryForm deliveryForm;
    private PaymentType paymentType;
    private Set<String> usedPromoCodes = new HashSet<>();


    public OrderSession() {
        this.order = new Order();
        this.deliveryForm = new DeliveryForm();
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
        this.customerForm = customerType == CustomerType.COMPANY ?
                new CompanyCustomerForm() :
                new IndividualCustomerForm();
    }

    public CompanyCustomerForm getCompanyForm() {
        if (customerForm instanceof CompanyCustomerForm) {
            return (CompanyCustomerForm) customerForm;
        }
        return null;
    }

    public IndividualCustomerForm getIndividualForm() {
        if (customerForm instanceof IndividualCustomerForm) {
            return (IndividualCustomerForm) customerForm;
        }
        return null;
    }

    // Reszta getterów i setterów bez zmian
    public CustomerType getCustomerType() {
        return customerType;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CustomerForm getCustomerForm() {
        return customerForm;
    }

    public void setCustomerForm(CustomerForm customerForm) {
        this.customerForm = customerForm;
    }

    public DeliveryForm getDeliveryForm() {
        return deliveryForm;
    }

    public void setDeliveryForm(DeliveryForm deliveryForm) {
        this.deliveryForm = deliveryForm;
    }

    public Set<String> getUsedPromoCodes() {
        return usedPromoCodes;
    }
}