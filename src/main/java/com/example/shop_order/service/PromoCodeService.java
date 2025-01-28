package com.example.shop_order.service;

import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.repository.PromoCodeRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Service for promo code entity
 */
@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    /**
     * Initializes promo codes in the database
     */
    @PostConstruct
    public void initPromoCodes() {
        PromoCode code1 = new PromoCode();
        code1.setCode("WELCOME10");
        code1.setDiscountPercentage(10.0);
        code1.setValidFrom(LocalDateTime.of(2025, 1, 1, 0, 0));
        code1.setValidTo(LocalDateTime.of(2025, 12, 31, 23, 59));
        code1.setIsActive(true);

        PromoCode code2 = new PromoCode();
        code2.setCode("SUMMER20");
        code2.setDiscountPercentage(20.0);
        code2.setValidFrom(LocalDateTime.of(2025, 6, 1, 0, 0));
        code2.setValidTo(LocalDateTime.of(2025, 8, 31, 23, 59));
        code2.setIsActive(true);

        promoCodeRepository.saveAll(Arrays.asList(code1, code2));
    }
}

