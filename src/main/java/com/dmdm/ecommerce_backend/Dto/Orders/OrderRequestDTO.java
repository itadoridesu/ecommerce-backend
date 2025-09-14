package com.dmdm.ecommerce_backend.Dto.Orders;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    @NotEmpty(message = "Order must have at least one order item")
    private List<OrderItemRequestDTO> orderItems;

    // Optional fields like shippingAddress, paymentMethod, etc. can be added here
}