package com.example.shop_order.repository;

import com.example.shop_order.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeAndIsActiveTrue(String code);

    @Query("SELECT p FROM PromoCode p WHERE p.code = :code " +
            "AND p.isActive = true " +
            "AND p.validFrom <= :now " +
            "AND p.validTo >= :now")
    Optional<PromoCode> findValidPromoCode(@Param("code") String code,
                                           @Param("now") LocalDateTime now);
}
