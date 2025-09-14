package com.dmdm.ecommerce_backend.Unit.Controller;

import com.dmdm.ecommerce_backend.Unit.Configuration.TestConfig;
import com.dmdm.ecommerce_backend.Dto.Products.ProductDTO;
import com.dmdm.ecommerce_backend.Services.Products.ProductService;
import com.dmdm.ecommerce_backend.enums.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO sampleDto;

    @BeforeEach
    void setup() {
        sampleDto = new ProductDTO();
        sampleDto.setId(1L);
        sampleDto.setName("Phone");
        sampleDto.setDescription("Smartphone");
        sampleDto.setPrice(200.0);
        sampleDto.setStockQuantity(10);
        sampleDto.setCategory(Category.ELECTRONICS);
    }

    @Test
    @WithMockUser
    void getAllProducts_shouldReturnPage() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(List.of(sampleDto));
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Phone"));
    }

    @Test
    @WithMockUser
    void getProductById_shouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createProduct_shouldReturnCreated() throws Exception {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(sampleDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createProductBulk_shouldReturnList() throws Exception {
        List<ProductDTO> list = List.of(sampleDto);
        when(productService.createProducts(any())).thenReturn(list);

        mockMvc.perform(post("/api/products/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Phone"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateProduct_shouldReturnUpdated() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(sampleDto);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void patchProduct_shouldReturnUpdated() throws Exception {
        Map<String, Object> updates = Map.of("price", 250.0);
        when(productService.updateProductPartially(eq(1L), any())).thenReturn(sampleDto);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @WithMockUser
    void searchProducts_shouldReturnResults() throws Exception {
        when(productService.searchProducts(any(), any(), any(), any(), any())).thenReturn(List.of(sampleDto));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Phone")
                        .param("category", Category.ELECTRONICS.name())
                        .param("minPrice", "100")
                        .param("maxPrice", "300")
                        .param("inStockOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Phone"));
    }
}
