package com.dmdm.ecommerce_backend.Repositories.Products;

import com.dmdm.ecommerce_backend.Entity.Products.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    public List<ProductImage> findByProductId (Long id);
}
