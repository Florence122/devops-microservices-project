package com.devops.orderservice.repository;

import com.devops.orderservice.entity.Order;
import com.devops.orderservice.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by user ID
    List<Order> findByUserId(Long userId);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by user and status
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    // Custom query to find orders with total amount greater than specified value
    @Query("SELECT o FROM Order o WHERE o.totalAmount > :amount")
    List<Order> findOrdersWithAmountGreaterThan(Double amount);
}