package com.selimhorri.app.service;

import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para ProductServiceImpl
 * Taller 2 - Pruebas y Lanzamiento
 *
 * ✅ NUEVA PRUEBA UNITARIA 6: testFindById_ProductExists_ReturnsProduct
 * ✅ NUEVA PRUEBA UNITARIA 7: testFindById_ProductNotExists_ThrowsException
 * ✅ NUEVA PRUEBA UNITARIA 8: testFindAll_ReturnsProductList
 * ✅ NUEVA PRUEBA UNITARIA 9: testSave_ValidProduct_SavesSuccessfully
 * ✅ NUEVA PRUEBA UNITARIA 10: testUpdate_ExistingProduct_UpdatesSuccessfully
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductTitle("Laptop Dell");
        testProduct.setImageUrl("http://test.com/laptop.jpg");
        testProduct.setSku("DELL-001");
        testProduct.setPriceUnit(1299.99);
        testProduct.setQuantity(50);
    }

    /**
     * ✅ PRUEBA UNITARIA 6: Validar que findById retorna un producto cuando existe
     */
    @Test
    void testFindById_ProductExists_ReturnsProduct() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act
        ProductDto result = productService.findById(1);

        // Assert
        assertNotNull(result, "El producto no debería ser nulo");
        assertEquals("Laptop Dell", result.getProductTitle(), "El título debe coincidir");
        assertEquals("DELL-001", result.getSku(), "El SKU debe coincidir");
        assertEquals(1299.99, result.getPriceUnit(), 0.01, "El precio debe coincidir");
        verify(productRepository, times(1)).findById(1);
    }

    /**
     * ✅ PRUEBA UNITARIA 7: Validar que findById lanza excepción cuando no existe
     */
    @Test
    void testFindById_ProductNotExists_ThrowsException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(
            ProductNotFoundException.class,
            () -> productService.findById(999),
            "Debería lanzar ProductNotFoundException"
        );

        assertTrue(exception.getMessage().contains("999"),
            "El mensaje debe contener el ID del producto");
        verify(productRepository, times(1)).findById(999);
    }

    /**
     * ✅ PRUEBA UNITARIA 8: Validar que findAll retorna lista de productos
     */
    @Test
    void testFindAll_ReturnsProductList() {
        // Arrange
        Product product2 = new Product();
        product2.setProductId(2);
        product2.setProductTitle("Mouse Logitech");
        product2.setSku("LOG-002");
        product2.setPriceUnit(29.99);
        product2.setQuantity(100);

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        // Act
        List<ProductDto> result = productService.findAll();

        // Assert
        assertNotNull(result, "La lista no debería ser nula");
        assertEquals(2, result.size(), "Debería retornar 2 productos");
        verify(productRepository, times(1)).findAll();
    }

    /**
     * ✅ PRUEBA UNITARIA 9: Validar que save guarda un producto correctamente
     */
    @Test
    void testSave_ValidProduct_SavesSuccessfully() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setProductTitle("Teclado Mecánico");
        productDto.setSku("KEY-003");
        productDto.setPriceUnit(89.99);
        productDto.setQuantity(75);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductDto result = productService.save(productDto);

        // Assert
        assertNotNull(result, "El producto guardado no debería ser nulo");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * ✅ PRUEBA UNITARIA 10: Validar que update actualiza un producto correctamente
     */
    @Test
    void testUpdate_ExistingProduct_UpdatesSuccessfully() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setProductId(1);
        productDto.setProductTitle("Laptop Dell Updated");
        productDto.setSku("DELL-001");
        productDto.setPriceUnit(1199.99);
        productDto.setQuantity(45);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductDto result = productService.update(productDto);

        // Assert
        assertNotNull(result, "El producto actualizado no debería ser nulo");
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
