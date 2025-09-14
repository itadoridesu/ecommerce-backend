package com.dmdm.ecommerce_backend.Unit.Configuration;

import com.dmdm.ecommerce_backend.Services.Products.ProductService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestConfig {
    @Mock
    private ProductService productService;

    @Bean
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }

}
