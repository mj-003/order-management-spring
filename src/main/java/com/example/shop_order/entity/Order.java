package com.example.shop_order.entity;
import com.example.shop_order.enums.DeliveryType;
import com.example.shop_order.enums.OrderStatus;
import com.example.shop_order.enums.PaymentType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.example.shop_order.model.Address;

@Entity
@Data
@Table(name = "orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    private Boolean homeDelivery;

    private LocalDateTime deliveryDate;
    private String inpostPoint;
    private String pickupPoint;

    @Column(nullable = false)
    private Double subtotal;

    private Double deliveryCost;
    private Double discount;

    private String promoCode;
    private Integer usedLoyaltyPoints;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String paymentTransactionId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;




    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", items=" + items +
                ", status=" + status +
                ", deliveryAddress=" + deliveryAddress +
                ", deliveryType=" + deliveryType +
                ", homeDelivery=" + homeDelivery +
                ", deliveryDate=" + deliveryDate +
                ", subtotal=" + subtotal +
                ", deliveryCost=" + deliveryCost +
                ", discount=" + discount +
                ", promoCode='" + promoCode + '\'' +
                ", usedLoyaltyPoints=" + usedLoyaltyPoints +
                ", paymentType=" + paymentType +
                ", paymentTransactionId='" + paymentTransactionId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public void setPhone(String phone) {
        this.deliveryAddress.setPhone(phone);
    }

    public void setInpostPoint(String inpostPoint) {
        this.inpostPoint = inpostPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public Boolean isHomeDelivery() {
        return homeDelivery;
    }
}

