package com.example.shop_order.DTOs;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.CustomerType;
import com.example.shop_order.enums.PaymentType;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderSession implements Serializable {
    private CustomerType customerType;
    private Order order;
    private CustomerForm customerForm;
    private DeliveryForm deliveryForm;
    private PaymentType paymentType;
    public OrderSession() {
        this.order = new Order();
        this.customerForm = new CustomerForm();
        this.deliveryForm = new DeliveryForm();
    }


    // Getters and setters
    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
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

}
