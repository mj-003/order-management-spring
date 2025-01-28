package com.example.shop_order.service;

import com.example.shop_order.entity.Product;
import com.example.shop_order.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for product entity
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Saves a product in the database
     *
     * @param product product to save
     * @return saved product
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Finds a product by its name
     *
     * @param name name of the product
     * @return product with the given name or null if not found
     */
    public Product findByName(String name) {
        return productRepository.findByName(name).orElse(null);
    }
}