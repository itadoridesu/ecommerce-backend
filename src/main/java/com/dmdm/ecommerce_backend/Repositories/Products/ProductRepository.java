package com.dmdm.ecommerce_backend.Repositories.Products;


import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    /// You could use method names as well (handled by spring data JPA,
    /// or even queryDSL which is a dependency)

    @Query("""
    SELECT p FROM Product p
    WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
      AND (:category IS NULL OR p.category = :category)
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:inStockOnly IS NULL OR (:inStockOnly = true AND p.stockQuantity > 0))
""")
    List<Product> searchProducts(
            @Param("name") String name,
            @Param("category") Category category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("inStockOnly") Boolean inStockOnly
    );

}
