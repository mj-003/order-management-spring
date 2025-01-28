package com.example.shop_order.entity;
import lombok.Data;
import javax.persistence.*;

/**
 * Entity class for products
 */
@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    private Boolean isLargeItem;

    public Product(long l, String name, double v, boolean b) {
        this.id = l;
        this.name = name;
        this.price = v;
        this.isLargeItem = b;

    }

    public Product() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getIsLargeItem() {
        return isLargeItem;
    }

    public void setIsLargeItem(Boolean isLargeItem) {
        this.isLargeItem = isLargeItem;
    }

    public void setLargeItem(boolean b) {
        this.isLargeItem = b;
    }
}
