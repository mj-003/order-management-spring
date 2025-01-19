package com.example.shop_order.entity;

import com.example.shop_order.model.Address;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import com.example.shop_order.enums.CustomerType;

@Entity
@Data
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Column(nullable = false)
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    // Dla firm
    private String companyName;
    private String nip;
    private String regon;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    private Integer loyaltyPoints = 0;

    public String toString() {
        return "Customer(id=" + this.getId() + ", type=" + this.getType() + ", email=" + this.getEmail() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", phoneNumber=" + this.getPhoneNumber() + ", companyName=" + this.getCompanyName() + ", nip=" + this.getNip() + ", regon=" + this.getRegon() + ", orders=" + this.getOrders() + ", loyaltyPoints=" + this.getLoyaltyPoints() + ")";
    }

}