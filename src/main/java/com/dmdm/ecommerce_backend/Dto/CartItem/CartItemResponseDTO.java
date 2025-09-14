package com.dmdm.ecommerce_backend.Dto.CartItem;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import lombok.Data;

@Data
public class CartItemResponseDTO {
    private Long id;
    private ProductDTO product;
    private int quantity;
}
