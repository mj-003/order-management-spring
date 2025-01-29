package com.example.shop_order.unitTests.repository;

import com.example.shop_order.entity.Product;
import com.example.shop_order.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByName_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        entityManager.persist(product);
        entityManager.flush();

        // Act
        Optional<Product> found = productRepository.findByName("Test Product");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
        assertThat(found.get().getPrice()).isEqualTo(99.99);
    }

    @Test
    void findByName_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Product> found = productRepository.findByName("Nonexistent Product");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void findByName_WithNullName_ShouldReturnEmpty() {
        // Act
        Optional<Product> found = productRepository.findByName(null);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistProduct() {
        // Arrange
        Product product = new Product();
        product.setName("New Product");
        product.setPrice(149.99);

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("New Product");
        assertThat(foundProduct.getPrice()).isEqualTo(149.99);
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(199.99);
        entityManager.persist(product);
        entityManager.flush();

        // Act
        Optional<Product> found = productRepository.findById(product.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
        assertThat(found.get().getPrice()).isEqualTo(199.99);
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(20.0);

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
        assertThat(products).extracting(Product::getPrice)
                .containsExactlyInAnyOrder(10.0, 20.0);
    }

    @Test
    void deleteById_ShouldRemoveProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Product to Delete");
        product.setPrice(299.99);
        entityManager.persist(product);
        entityManager.flush();

        // Act
        productRepository.deleteById(product.getId());

        // Assert
        Product foundProduct = entityManager.find(Product.class, product.getId());
        assertThat(foundProduct).isNull();
    }

    @Test
    void findByName_ShouldBeCaseSensitive() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        entityManager.persist(product);
        entityManager.flush();

        // Act
        Optional<Product> foundExact = productRepository.findByName("Test Product");
        Optional<Product> foundLower = productRepository.findByName("test product");
        Optional<Product> foundUpper = productRepository.findByName("TEST PRODUCT");

        // Assert
        assertThat(foundExact).isPresent();
        assertThat(foundLower).isEmpty();
        assertThat(foundUpper).isEmpty();
    }

    @Test
    void save_ShouldUpdateExistingProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Initial Name");
        product.setPrice(100.0);
        Product savedProduct = entityManager.persist(product);
        entityManager.flush();

        // Act
        savedProduct.setName("Updated Name");
        savedProduct.setPrice(150.0);
        productRepository.save(savedProduct);

        // Assert
        Product updatedProduct = entityManager.find(Product.class, savedProduct.getId());
        assertThat(updatedProduct.getName()).isEqualTo("Updated Name");
        assertThat(updatedProduct.getPrice()).isEqualTo(150.0);
    }
}