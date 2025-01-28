package com.example.shop_order.entity;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class for promo codes
 */
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

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && validFrom.isBefore(now) && validTo.isAfter(now);
    }
}