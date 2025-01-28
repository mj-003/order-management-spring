package com.example.shop_order.repository;

import com.example.shop_order.entity.Customer;
import com.example.shop_order.entity.Order;
import com.example.shop_order.entity.PromoCode;
import com.example.shop_order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
