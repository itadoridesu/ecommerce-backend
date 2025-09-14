package com.dmdm.ecommerce_backend.Services.Products;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Exception.ProductNotFoundException;
import com.dmdm.ecommerce_backend.Mapper.ProductMapper;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDTO); // Map each Product → ProductDTO
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDTO(product);
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    public List<ProductDTO> createProducts(List<ProductDTO> dtoList) {
        List<Product> products = productMapper.toEntityList(dtoList);
        return productMapper.toDtoList(productRepository.saveAll(products));
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(dto.getName());
                    product.setDescription(dto.getDescription());
                    product.setPrice(dto.getPrice());
                    product.setStockQuantity(dto.getStockQuantity());
                    product.setCategory(dto.getCategory());
                    return productMapper.toDTO(productRepository.save(product));
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


    public ProductDTO updateProductPartially(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> product.setName((String) value);
                case "description" -> product.setDescription((String) value);
                case "price" -> product.setPrice(Double.valueOf(value.toString()));
                case "stockQuantity" -> product.setStockQuantity((Integer) value);
                // Add more fields as needed
            }
        });

        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }


    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDTO> searchProducts(
            String name,
            Category category,
            Double minPrice,
            Double maxPrice,
            Boolean inStockOnly
    ) {
        List<Product> products = productRepository.searchProducts(name, category, minPrice, maxPrice, inStockOnly);
        return productMapper.toDtoList(products);
    }


}