package com.dmdm.ecommerce_backend.Entity;

import com.dmdm.ecommerce_backend.Entity.Products.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many cart items can belong to one user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Foreign key column in cart_items table
    private User user;

    // Many cart items can refer to the same product
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // Foreign key column in cart_items table
    private Product product;

    private int quantity;
}
