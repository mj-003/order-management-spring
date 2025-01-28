package com.example.shop_order.service;

import com.example.shop_order.entity.Product;
import com.example.shop_order.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void save_ShouldReturnSavedProduct() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        when(productRepository.save(product)).thenReturn(product);

        // When
        Product savedProduct = productService.save(product);

        // Then
        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        verify(productRepository).save(product);
    }

    @Test
    void findByName_WhenProductExists_ShouldReturnProduct() {
        // Given
        String productName = "Existing Product";
        Product product = new Product();
        product.setName(productName);
        when(productRepository.findByName(productName)).thenReturn(Optional.of(product));

        // When
        Product foundProduct = productService.findByName(productName);

        // Then
        assertNotNull(foundProduct);
        assertEquals(productName, foundProduct.getName());
        verify(productRepository).findByName(productName);
    }

    @Test
    void findByName_WhenProductDoesNotExist_ShouldReturnNull() {
        // Given
        String productName = "Non-existing Product";
        when(productRepository.findByName(productName)).thenReturn(Optional.empty());

        // When
        Product foundProduct = productService.findByName(productName);

        // Then
        assertNull(foundProduct);
        verify(productRepository).findByName(productName);
    }
}