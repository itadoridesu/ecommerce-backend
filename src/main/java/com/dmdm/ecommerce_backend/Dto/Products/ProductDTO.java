package com.dmdm.ecommerce_backend.Dto.Products;

import com.dmdm.ecommerce_backend.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @Positive(message = "Price must be positive")
    private Double price;

    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private Category category;

    private List<ProductImageResponse> images;
}