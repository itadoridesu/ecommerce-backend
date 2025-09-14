package com.dmdm.ecommerce_backend.Repositories.Orders;

import com.dmdm.ecommerce_backend.Entity.Orders.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}