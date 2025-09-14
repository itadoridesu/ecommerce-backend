package com.dmdm.ecommerce_backend.Entity.Products;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id") // rah ykoun id tae product
    private Product product;

    private String imageUrl;

}
