package com.ctbe.productservice;

import com.ctbe.productservice.model.Product;
import com.ctbe.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        testProduct = new Product("Test Laptop", 999.0, 10, "Electronics");
        productRepository.save(testProduct);
    }

    @Test
    void getAll_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Test Laptop")));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Laptop")));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource Not Found")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void create_returns201_withLocation() throws Exception {
        String requestBody = """
                {
                    "name": "New Smartphone",
                    "price": 699.99,
                    "stockQty": 50,
                    "category": "Electronics"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("New Smartphone")));
    }

    @Test
    void create_returns400_whenNameBlank() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "price": 699.99,
                    "stockQty": 50,
                    "category": "Electronics"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.detail", is("Name is required")));
    }

    @Test
    void create_returns400_whenPriceInvalid() throws Exception {
        String requestBody = """
                {
                    "name": "New Smartphone",
                    "price": -10.0,
                    "stockQty": 50,
                    "category": "Electronics"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.detail", is("Price must be greater than 0")));
    }

    @Test
    void update_returns200() throws Exception {
        String requestBody = """
                {
                    "name": "Updated Laptop",
                    "price": 899.0,
                    "stockQty": 15,
                    "category": "Electronics"
                }
                """;

        mockMvc.perform(put("/api/v1/products/{id}", testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(899.0)));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", testProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", 9999L))
                .andExpect(status().isNotFound());
    }
}
