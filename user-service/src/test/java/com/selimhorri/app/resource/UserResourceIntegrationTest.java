package com.selimhorri.app.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para UserResource
 * Taller 2 - Pruebas y Lanzamiento
 *
 * Estas pruebas validan la integración completa del endpoint REST
 * con Spring Boot, incluyendo serialización JSON, validaciones,
 * y respuestas HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * PRUEBA DE INTEGRACIÓN 1: GET /api/users
     * Valida que el endpoint devuelve lista de usuarios en formato JSON
     */
    @Test
    void testGetAllUsers_ReturnsUserListAsJson() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * PRUEBA DE INTEGRACIÓN 2: GET /api/users/{userId}
     * Valida que el endpoint devuelve un usuario específico
     */
    @Test
    void testGetUserById_ExistingUser_ReturnsUser() throws Exception {
        // Usar ID 1 que debería existir en datos de prueba
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.userId").value(1));
    }

    /**
     * PRUEBA DE INTEGRACIÓN 3: GET /api/users con Accept header
     * Valida content negotiation (JSON)
     */
    @Test
    void testGetAllUsers_WithAcceptHeader_ReturnsJson() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Accept", "application/json"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
