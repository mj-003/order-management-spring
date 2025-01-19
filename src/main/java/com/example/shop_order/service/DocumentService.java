package com.example.shop_order.service;

import com.example.shop_order.entity.Order;
import com.example.shop_order.enums.CustomerType;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;


@Service
public class DocumentService {
    private final EmailService emailService;

    public DocumentService(EmailService emailService) {
        this.emailService = emailService;
    }

    public Document generateDocument(Order order) {
        //Document document = new Document();
        if (order.getCustomer().getType() == CustomerType.COMPANY) {
            return generateInvoice(order);
        } else {
            return generateReceipt(order);
        }
    }

    private Document generateInvoice(Order order) {
        // Logika generowania faktury
        return null;
    }

    private Document generateReceipt(Order order) {
        // Logika generowania paragonu
        return null;
    }
}

