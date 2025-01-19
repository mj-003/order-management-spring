package com.example.shop_order.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "promo_codes")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double discountPercentage;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isActive;
}