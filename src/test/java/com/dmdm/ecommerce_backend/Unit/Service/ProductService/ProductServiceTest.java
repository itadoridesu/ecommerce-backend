package com.dmdm.ecommerce_backend.Unit.Service.ProductService;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Exception.ProductNotFoundException;
import com.dmdm.ecommerce_backend.Mapper.ProductMapper;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.Services.Products.ProductService;
import com.dmdm.ecommerce_backend.enums.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enable Mockito for this test class
class ProductServiceTest {

    // 👇 Mock the ProductRepository so we don’t hit a real database
    @Mock
    private ProductRepository productRepository;

    // 👇 Mock the ProductMapper to avoid depending on real mapping logic
    @Mock
    private ProductMapper productMapper;

    // 👇 Inject the mocks into a real ProductService instance
    @InjectMocks
    private ProductService productService;

    @Test
    void testGetProductById() {

        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("A sample product")
                .price(19.99)
                .stockQuantity(100)
                .category(Category.ELECTRONICS)
                .images(new ArrayList<>()) // No images in this test
                .build();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Test Product");
        productDTO.setDescription("A sample product");
        productDTO.setPrice(19.99);
        productDTO.setStockQuantity(100);
        productDTO.setCategory(Category.ELECTRONICS);
        productDTO.setImages(List.of()); // No images in this test

        // 👇 Tell the mock repository what to return when called
        // "When someone calls findById(productId), return Optional.of(product)"
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // 👇 Tell the mock mapper what to return when called
        // "When someone maps this product, return this prepared DTO"
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // ✅ When: Call the method under test
        ProductDTO result = productService.getProductById(productId);

        // ✅ Then: Assert the results are what we expect
        assertNotNull(result); // Check the result isn’t null
        assertEquals(productId, result.getId()); // ID should match
        assertEquals("Test Product", result.getName()); // Name should match
        assertEquals("A sample product", result.getDescription()); // Description match
        assertEquals(19.99, result.getPrice()); // Price match
        assertEquals(100, result.getStockQuantity()); // Stock quantity match
        assertEquals(Category.ELECTRONICS, result.getCategory()); // Category match
        assertTrue(result.getImages().isEmpty()); // No images expected

        // ✅ Verify that the repository was called exactly once with the correct ID
        verify(productRepository, times(1)).findById(productId);

        // ✅ Verify that the mapper was called once to convert the product
        verify(productMapper, times(1)).toDTO(product);
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        Pageable pageable = Pageable.ofSize(2); // simulate pagination (2 items per page)

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(10.0)
                .stockQuantity(5)
                .category(Category.BOOKS)
                .images(new ArrayList<>())
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Description 2")
                .price(20.0)
                .stockQuantity(10)
                .category(Category.ELECTRONICS)
                .images(new ArrayList<>())
                .build();

        List<Product> products = List.of(product1, product2);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        ProductDTO dto1 = new ProductDTO();
        dto1.setId(1L);
        dto1.setName("Product 1");
        dto1.setDescription("Description 1");
        dto1.setPrice(10.0);
        dto1.setStockQuantity(5);
        dto1.setCategory(Category.BOOKS);
        dto1.setImages(List.of());

        ProductDTO dto2 = new ProductDTO();
        dto2.setId(2L);
        dto2.setName("Product 2");
        dto2.setDescription("Description 2");
        dto2.setPrice(20.0);
        dto2.setStockQuantity(10);
        dto2.setCategory(Category.ELECTRONICS);
        dto2.setImages(List.of());

        // Mock behavior
        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDTO(product1)).thenReturn(dto1);
        when(productMapper.toDTO(product2)).thenReturn(dto2);

        // Act
        Page<ProductDTO> result = productService.getAllProducts(pageable);

        // Assert
        assertEquals(2, result.getContent().size());

        ProductDTO result1 = result.getContent().get(0);
        ProductDTO result2 = result.getContent().get(1);

        assertEquals("Product 1", result1.getName());
        assertEquals("Product 2", result2.getName());

        // Verify repository and mapper calls
        verify(productRepository, times(1)).findAll(pageable);
        verify(productMapper, times(1)).toDTO(product1);
        verify(productMapper, times(1)).toDTO(product2);
    }

    @Test
    void testCreateProduct() {

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(10.0)
                .stockQuantity(5)
                .category(Category.BOOKS)
                .images(new ArrayList<>())
                .build();

        ProductDTO dto1 = new ProductDTO();
        dto1.setId(1L);
        dto1.setName("Product 1");
        dto1.setDescription("Description 1");
        dto1.setPrice(10.0);
        dto1.setStockQuantity(5);
        dto1.setCategory(Category.BOOKS);
        dto1.setImages(List.of());

        when(productMapper.toEntity(dto1)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(product1);
        when(productMapper.toDTO(product1)).thenReturn(dto1);


        ProductDTO result = productService.createProduct(dto1);

        assertNotNull(result);
        assertEquals("Product 1", result.getName());


    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Name")
                .description("Old Desc")
                .price(10.0)
                .stockQuantity(5)
                .category(Category.BOOKS)
                .images(new ArrayList<>())
                .build();

        ProductDTO updateDto = new ProductDTO();
        updateDto.setName("New Name");
        updateDto.setDescription("New Desc");
        updateDto.setPrice(20.0);
        updateDto.setStockQuantity(15);
        updateDto.setCategory(Category.ELECTRONICS);

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("New Name")
                .description("New Desc")
                .price(20.0)
                .stockQuantity(15)
                .category(Category.ELECTRONICS)
                .images(new ArrayList<>())
                .build();

        ProductDTO resultDto = new ProductDTO();
        resultDto.setId(productId);
        resultDto.setName("New Name");
        resultDto.setDescription("New Desc");
        resultDto.setPrice(20.0);
        resultDto.setStockQuantity(15);
        resultDto.setCategory(Category.ELECTRONICS);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toDTO(updatedProduct)).thenReturn(resultDto);

        ProductDTO result = productService.updateProduct(productId, updateDto);

        assertEquals("New Name", result.getName());
        assertEquals(20.0, result.getPrice());
    }

    @Test
    void testUpdateProductPartially() {
        Long productId = 1L;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Name")
                .description("Old Desc")
                .price(10.0)
                .stockQuantity(5)
                .category(Category.BOOKS)
                .images(new ArrayList<>())
                .build();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Partially Updated");
        updates.put("price", 99.99);

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Partially Updated")
                .description("Old Desc")
                .price(99.99)
                .stockQuantity(5)
                .category(Category.BOOKS)
                .images(new ArrayList<>())
                .build();

        ProductDTO resultDto = new ProductDTO();
        resultDto.setId(productId);
        resultDto.setName("Partially Updated");
        resultDto.setDescription("Old Desc");
        resultDto.setPrice(99.99);
        resultDto.setStockQuantity(5);
        resultDto.setCategory(Category.BOOKS);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toDTO(updatedProduct)).thenReturn(resultDto);

        ProductDTO result = productService.updateProductPartially(productId, updates);

        assertEquals("Partially Updated", result.getName());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    void testDeleteProductSuccess() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testDeleteProductNotFound() {
        Long productId = 999L;

        when(productRepository.existsById(productId)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void testSearchProducts() {
        String name = "Product";
        Category category = Category.ELECTRONICS;
        Double minPrice = 10.0;
        Double maxPrice = 100.0;
        Boolean inStockOnly = true;

        Product p1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Desc 1")
                .price(20.0)
                .stockQuantity(10)
                .category(category)
                .images(new ArrayList<>())
                .build();

        Product p2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Desc 2")
                .price(50.0)
                .stockQuantity(7)
                .category(category)
                .images(new ArrayList<>())
                .build();

        List<Product> productList = List.of(p1, p2);

        ProductDTO dto1 = new ProductDTO();
        dto1.setId(1L);
        dto1.setName("Product 1");

        ProductDTO dto2 = new ProductDTO();
        dto2.setId(2L);
        dto2.setName("Product 2");

        when(productRepository.searchProducts(name, category, minPrice, maxPrice, inStockOnly)).thenReturn(productList);
        when(productMapper.toDtoList(productList)).thenReturn(List.of(dto1, dto2));

        List<ProductDTO> result = productService.searchProducts(name, category, minPrice, maxPrice, inStockOnly);

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }
}

