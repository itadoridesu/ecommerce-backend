package com.dmdm.ecommerce_backend.helpers;

import com.dmdm.ecommerce_backend.Entity.CartItem;
import com.dmdm.ecommerce_backend.Entity.Orders.Order;
import com.dmdm.ecommerce_backend.Entity.Orders.OrderItem;
import com.dmdm.ecommerce_backend.Entity.User;
import com.dmdm.ecommerce_backend.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHelper {

    public static Order fromCartItems(List<CartItem> cartItems, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order); // important to maintain the bidirectional link
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPriceAtPurchase(cartItem.getProduct().getPrice());
            return item;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        double totalAmount = orderItems.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPriceAtPurchase())
                .sum();
        order.setTotalAmount(totalAmount);

        return order;
    }

}
