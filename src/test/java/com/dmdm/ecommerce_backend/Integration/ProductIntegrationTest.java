package com.dmdm.ecommerce_backend.Integration;

import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import com.dmdm.ecommerce_backend.Entity.Products.Product;
import com.dmdm.ecommerce_backend.Repositories.Products.ProductRepository;
import com.dmdm.ecommerce_backend.enums.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // PostgreSQL Docker container for testing
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void clearDB() {
        productRepository.deleteAll();
    }

    private Product createValidProduct(String name) {
        Product p = new Product();
        p.setName(name);
        p.setDescription("Some description");
        p.setPrice(100.0);
        p.setStockQuantity(10);
        p.setCategory(Category.ELECTRONICS);
        return p;
    }

    private ProductDTO createSampleDto(String name) {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setDescription("Default description");
        dto.setPrice(100.0);
        dto.setStockQuantity(10);
        dto.setCategory(Category.ELECTRONICS);
        return dto;
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_thenVerifyInDB() throws Exception {
        ProductDTO dto = createSampleDto("TV");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TV"));

        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals("TV", products.get(0).getName());
    }

    @Test
    @WithMockUser
    void getProductById_shouldReturnProduct() throws Exception {
        Product saved = productRepository.save(createValidProduct("TV"));

        mockMvc.perform(get("/api/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TV"));
    }

    @Test
    @WithMockUser
    void getAllProducts_shouldReturnPagedProducts() throws Exception {
        productRepository.save(createValidProduct("TV"));
        productRepository.save(createValidProduct("Phone"));

        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_shouldChangeFields() throws Exception {
        Product saved = productRepository.save(createValidProduct("Old Name"));

        ProductDTO updated = createSampleDto("New Name");

        mockMvc.perform(put("/api/products/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        assertEquals("New Name", productRepository.findById(saved.getId()).get().getName());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void patchProduct_shouldUpdateSingleField() throws Exception {
        Product saved = productRepository.save(createValidProduct("Patchable"));

        Map<String, Object> updates = Map.of("price", 999.0);

        mockMvc.perform(patch("/api/products/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(999.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldRemoveIt() throws Exception {
        Product saved = productRepository.save(createValidProduct("ToDelete"));

        mockMvc.perform(delete("/api/products/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertFalse(productRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @WithMockUser
    void searchProduct_shouldReturnMatch() throws Exception {
        productRepository.save(createValidProduct("Samsung TV"));
        productRepository.save(createValidProduct("iPhone"));

        mockMvc.perform(get("/api/products/search?name=Samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Samsung TV"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createBulkProducts_shouldWork() throws Exception {
        List<ProductDTO> dtoList = List.of(
                createSampleDto("Laptop"),
                createSampleDto("Camera")
        );

        mockMvc.perform(post("/api/products/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // Unauthorized & unauthenticated user

//    @Test
//    void createProduct_shouldReturn401_forUnauthenticatedUser() throws Exception {
//        ProductDTO dto = createSampleDto("Laptop");
//
//        mockMvc.perform(post("/api/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isUnauthorized()); // 401
//    }
//
//    @WithMockUser(roles = "USER")
//    @Test
//    void createProduct_shouldReturn403_forUnauthorizedUser() throws Exception {
//        ProductDTO dto = createSampleDto("Laptop");
//
//        mockMvc.perform(post("/api/products")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isForbidden()); // 403
//    }

}

