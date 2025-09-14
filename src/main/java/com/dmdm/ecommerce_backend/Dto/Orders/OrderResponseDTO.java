package com.dmdm.ecommerce_backend.Dto.Orders;

import com.dmdm.ecommerce_backend.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;

    // List of order items DTOs
    private List<OrderItemResponseDTO> orderItems;
}
