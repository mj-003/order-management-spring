package com.example.shop_order.service;

import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.repository.PromoCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Captor
    private ArgumentCaptor<List<PromoCode>> promoCodesCaptor;

    private PromoCodeService promoCodeService;

    @BeforeEach
    void setUp() {
        promoCodeService = new PromoCodeService(promoCodeRepository);
    }

    @Test
    void initPromoCodes_ShouldInitializeTwoPromoCodes() {
        // When
        promoCodeService.initPromoCodes();

        // Then
        verify(promoCodeRepository).saveAll(promoCodesCaptor.capture());
        List<PromoCode> savedCodes = promoCodesCaptor.getValue();

        assertEquals(2, savedCodes.size());

        // Verify WELCOME10 code
        PromoCode welcome10 = savedCodes.stream()
                .filter(code -> code.getCode().equals("WELCOME10"))
                .findFirst()
                .orElseThrow();

        assertEquals(10.0, welcome10.getDiscountPercentage());
        assertEquals(
                LocalDateTime.of(2025, 1, 1, 0, 0),
                welcome10.getValidFrom()
        );
        assertEquals(
                LocalDateTime.of(2025, 12, 31, 23, 59),
                welcome10.getValidTo()
        );
        assertTrue(welcome10.getIsActive());

        // Verify SUMMER20 code
        PromoCode summer20 = savedCodes.stream()
                .filter(code -> code.getCode().equals("SUMMER20"))
                .findFirst()
                .orElseThrow();

        assertEquals(20.0, summer20.getDiscountPercentage());
        assertEquals(
                LocalDateTime.of(2025, 6, 1, 0, 0),
                summer20.getValidFrom()
        );
        assertEquals(
                LocalDateTime.of(2025, 8, 31, 23, 59),
                summer20.getValidTo()
        );
        assertTrue(summer20.getIsActive());
    }

    @Test
    void initPromoCodes_ShouldCallRepositoryOnce() {
        // When
        promoCodeService.initPromoCodes();

        // Then
        verify(promoCodeRepository, times(1)).saveAll(any());
    }
}