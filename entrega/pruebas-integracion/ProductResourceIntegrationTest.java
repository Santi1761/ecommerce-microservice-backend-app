package com.selimhorri.app.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ProductResource
 * Taller 2 - Pruebas y Lanzamiento
 *
 * Estas pruebas validan la integración completa del endpoint REST
 * de productos con Spring Boot.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * PRUEBA DE INTEGRACIÓN 4: GET /api/products
     * Valida que el endpoint devuelve lista de productos en formato JSON
     */
    @Test
    void testGetAllProducts_ReturnsProductListAsJson() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * PRUEBA DE INTEGRACIÓN 5: GET /api/products/{productId}
     * Valida que el endpoint devuelve un producto específico
     */
    @Test
    void testGetProductById_ExistingProduct_ReturnsProduct() throws Exception {
        // Usar ID 1 que debería existir en datos de prueba
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.productId").value(1));
    }
}
