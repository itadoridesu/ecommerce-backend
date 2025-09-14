package com.dmdm.ecommerce_backend.Services;

import com.dmdm.ecommerce_backend.Dto.Products.ProductImageResponse;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Entity.Products.ProductImage;
import com.dmdm.ecommerce_backend.Exception.ProductNotFoundException;
import com.dmdm.ecommerce_backend.Mapper.ProductMapper;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductImageRepository;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.helpers.ImageStorageUtility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ImageStorageUtility imageStorageUtility;
    private final ProductMapper productMapper;


    public ProductImageService(ProductRepository productRepository,
                               ProductImageRepository productImageRepository,
                               ImageStorageUtility imageStorageUtility,
                               ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.imageStorageUtility = imageStorageUtility;
        this.productMapper = productMapper;
    }

    public List<ProductImageResponse> saveProductImages(Long productId, MultipartFile[] files) throws IOException {

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one image must be uploaded.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<ProductImageResponse> savedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = imageStorageUtility.saveImage(file);

            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imageUrl);

            ProductImage saved = productImageRepository.save(productImage);
            savedImages.add(productMapper.toProductImageResponseDTO(saved));
        }

        return savedImages;
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(productMapper::toProductImageResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductImageResponse updateProductImage(
            Long productId, Long imageId, MultipartFile file) throws IOException {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        ProductImage productImage = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Image with id " + imageId + " not found"));

        String oldImageFilename = Paths.get(productImage.getImageUrl()).getFileName().toString();
        imageStorageUtility.deleteImageFile(oldImageFilename);

        productImage.setImageUrl(imageStorageUtility.saveImage(file));

        productImageRepository.save(productImage);

        return productMapper.toProductImageResponseDTO(productImage);
    }

    public void deleteProductImage(Long productId, Long imageId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        ProductImage productImage = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Image with id " + imageId + " not found"));

        // ✅ Extract the filename from the image URL (just the last part)
        String imageFilename = Paths.get(productImage.getImageUrl()).getFileName().toString();

        imageStorageUtility.deleteImageFile(imageFilename);

        // 2. Remove from product's list
        product.getImages().remove(productImage);

        // 3. Delete from DB
        productImageRepository.delete(productImage);
    }


}
