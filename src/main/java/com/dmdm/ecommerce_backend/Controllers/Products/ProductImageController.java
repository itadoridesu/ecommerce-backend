package com.dmdm.ecommerce_backend.Controllers.Products;

import com.dmdm.ecommerce_backend.Dto.Products.ProductImageResponse;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Services.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping("/{productId}/images/batch")
    public ResponseEntity<List<ProductImageResponse>> uploadMultipleImages(
            @PathVariable Long productId,
            @RequestParam("images") MultipartFile[] files) throws IOException {
        List<ProductImageResponse> responseList = productImageService.saveProductImages(productId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseList);
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponse>> getImagesForProduct(
            @PathVariable Long productId
    ) {
        List<ProductImageResponse> productImages = productImageService.getImagesForProduct(productId);
        return ResponseEntity.ok(productImages);
    }

    @PatchMapping("/{productId}/update/images/{imageId}")
    public ResponseEntity<ProductImageResponse> updateProductImage(
            @PathVariable Long productId, @PathVariable Long imageId, @RequestParam("images") MultipartFile file
    ) throws IOException {
        ProductImageResponse productImageResponse = productImageService.updateProductImage(productId, imageId, file);
        return ResponseEntity.ok(productImageResponse);
    }

    @DeleteMapping("/{productId}/delete/images/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) throws IOException {
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}
