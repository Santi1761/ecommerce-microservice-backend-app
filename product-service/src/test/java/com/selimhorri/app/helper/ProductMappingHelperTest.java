package com.selimhorri.app.helper;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductMappingHelperTest {

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();

        testProduct = Product.builder()
                .productId(1)
                .productTitle("Laptop Dell XPS 15")
                .imageUrl("http://example.com/laptop.jpg")
                .sku("DELL-XPS-15")
                .priceUnit(1499.99)
                .quantity(25)
                .category(testCategory)
                .build();
    }

    /**
     * ✅ PRUEBA UNITARIA 6: Validar mapeo de Product a ProductDto
     */
    @Test
    void testMapProductToDto_ValidProduct_MapsCorrectly() {
        // Act
        ProductDto result = ProductMappingHelper.map(testProduct);

        // Assert
        assertNotNull(result, "El ProductDto no debería ser nulo");
        assertEquals(1, result.getProductId(), "El productId debe coincidir");
        assertEquals("Laptop Dell XPS 15", result.getProductTitle(), "El título debe coincidir");
        assertEquals("DELL-XPS-15", result.getSku(), "El SKU debe coincidir");
        assertEquals(1499.99, result.getPriceUnit(), 0.01, "El precio debe coincidir");
        assertEquals(25, result.getQuantity(), "La cantidad debe coincidir");
        assertEquals("http://example.com/laptop.jpg", result.getImageUrl(), "La imageUrl debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 7: Validar mapeo de ProductDto a Product
     */
    @Test
    void testMapDtoToProduct_ValidDto_MapsCorrectly() {
        // Arrange
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(2)
                .categoryTitle("Accessories")
                .build();

        ProductDto productDto = ProductDto.builder()
                .productId(2)
                .productTitle("Mouse Logitech MX Master")
                .sku("LOG-MX-MASTER")
                .priceUnit(99.99)
                .quantity(150)
                .categoryDto(categoryDto)
                .build();

        // Act
        Product result = ProductMappingHelper.map(productDto);

        // Assert
        assertNotNull(result, "El Product no debería ser nulo");
        assertEquals(2, result.getProductId(), "El productId debe coincidir");
        assertEquals("Mouse Logitech MX Master", result.getProductTitle(), "El título debe coincidir");
        assertEquals("LOG-MX-MASTER", result.getSku(), "El SKU debe coincidir");
        assertEquals(99.99, result.getPriceUnit(), 0.01, "El precio debe coincidir");
        assertEquals(150, result.getQuantity(), "La cantidad debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 8: Validar que el mapping de Product contiene Category
     */
    @Test
    void testMapProduct_WithCategory_IncludesCategoryData() {
        // Act
        ProductDto result = ProductMappingHelper.map(testProduct);

        // Assert
        assertNotNull(result, "El ProductDto no debería ser nulo");
        assertNotNull(result.getCategoryDto(), "El CategoryDto no debería ser nulo");
        assertEquals(1, result.getCategoryDto().getCategoryId(), "El categoryId debe coincidir");
        assertEquals("Electronics", result.getCategoryDto().getCategoryTitle(),
                "El título de categoría debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 9: Validar manejo de campos nulos en imageUrl
     */
    @Test
    void testMapProduct_WithNullImageUrl_HandlesGracefully() {
        // Arrange
        Product productWithNullImage = Product.builder()
                .productId(3)
                .productTitle("Producto sin imagen")
                .sku("NO-IMG-001")
                .priceUnit(29.99)
                .quantity(50)
                .imageUrl(null)
                .category(testCategory)
                .build();

        // Act
        ProductDto result = ProductMappingHelper.map(productWithNullImage);

        // Assert
        assertNotNull(result, "El ProductDto no debería ser nulo");
        assertNull(result.getImageUrl(), "La imageUrl debe ser nula");
        assertEquals("Producto sin imagen", result.getProductTitle(), "El título debe coincidir");
    }

    /**
     * ✅ PRUEBA UNITARIA 10: Validar mapeo correcto de precio y cantidad
     */
    @Test
    void testMapProduct_PriceAndQuantity_MapsCorrectly() {
        // Arrange
        Product expensiveProduct = Product.builder()
                .productId(4)
                .productTitle("MacBook Pro 16\"")
                .sku("APPLE-MBP-16")
                .priceUnit(2999.99)
                .quantity(10)
                .category(testCategory)
                .build();

        // Act
        ProductDto result = ProductMappingHelper.map(expensiveProduct);

        // Assert
        assertAll("Verificar precio y cantidad",
                () -> assertEquals(2999.99, result.getPriceUnit(), 0.01, "El precio debe ser exacto"),
                () -> assertEquals(10, result.getQuantity(), "La cantidad debe coincidir"),
                () -> assertTrue(result.getPriceUnit() > 2000, "El precio debe ser mayor a 2000"),
                () -> assertTrue(result.getQuantity() > 0, "La cantidad debe ser positiva"));
    }
}
