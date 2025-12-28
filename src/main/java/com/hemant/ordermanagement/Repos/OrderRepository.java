package com.hemant.ordermanagement.Repos;

import com.hemant.ordermanagement.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);
}