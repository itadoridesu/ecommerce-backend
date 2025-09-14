package com.dmdm.ecommerce_backend.Repositories.Orders;


import com.dmdm.ecommerce_backend.Entity.Orders.Order;
import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserAndStatus (User user, OrderStatus status);
}
