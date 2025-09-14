package com.dmdm.ecommerce_backend.Dto.Orders;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private int quantity;
    private double priceAtPurchase;
    private ProductDTO product; // Embed product info using your existing ProductDTO
}
