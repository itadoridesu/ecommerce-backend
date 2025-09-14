package com.dmdm.ecommerce_backend.Mapper;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import com.dmdm.ecommerce_backend.Dto.Products.ProductImageResponse;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Entity.Products.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategory(product.getCategory());

        if (product.getImages() != null) {
            List<ProductImageResponse> imageDTOs = product.getImages()
                    .stream()
                    .map(this::toProductImageResponseDTO)
                    .collect(Collectors.toList());

            dto.setImages(imageDTOs);
        }

        return dto;
    }


    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());

        return product;
    }

    public List<ProductDTO> toDtoList(List<Product> products) {
        if (products == null) return null;
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Product> toEntityList(List<ProductDTO> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public ProductImageResponse toProductImageResponseDTO(ProductImage entity) {
        if (entity == null) return null;

        ProductImageResponse dto = new ProductImageResponse();
        dto.setId(entity.getId());
        dto.setImage(entity.getImageUrl());
        return dto;
    }
}

//@Mapper(componentModel = "spring")
//public interface ProductMapper {
//    ProductDTO toDTO(Product product);
//    Product toEntity(ProductDTO dto);
//
//    List<Product> toEntityList(List<ProductDTO> dtoList);
//
//    List<ProductDTO> toDtoList(List<Product> products);
//
//
//}
