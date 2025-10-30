package com.core.core;

import com.core.core.controllers.ProductController;
import com.core.core.modules.Product;
import com.core.core.modules.ProductType;
import com.core.core.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProductController productController;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ProductType productType = new ProductType();
        productType.setTypeCode(1L);
        productType.setTypeName("Electrónico");

        sampleProduct = Product.builder()
                .proCode(1L)
                .proName("Televisor LG")
                .proImg("img.jpg")
                .proPrice(BigDecimal.valueOf(2500000))
                .descript("Televisor inteligente 55 pulgadas")
                .proMark("LG")
                .status("A")
                .productType(productType)
                .build();
    }

    // =====================================================
    // === TESTS DE MÉTODOS NORMALES =======================
    // =====================================================

    @Test
    void testGetAllProducts_Empty() {
        when(productService.getProducts()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetAllProducts_Success() {
        when(productService.getProducts()).thenReturn(List.of(sampleProduct));

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetProduct_NotFound() {
        when(productService.getProduct(99L)).thenReturn(null);

        ResponseEntity<?> response = productController.getProduct(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetProduct_Success() {
        when(productService.getProduct(1L)).thenReturn(sampleProduct);

        ResponseEntity<?> response = productController.getProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleProduct, response.getBody());
    }

    @Test
    void testCreateProduct() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.createProduct(any(Product.class))).thenReturn(sampleProduct);

        // Simular un request HTTP (para evitar el error de ServletUriComponentsBuilder)
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<?> response = productController.createProduct(sampleProduct, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleProduct, response.getBody());
    }

    @Test
    void testCreateProduct_WithValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = productController.createProduct(sampleProduct, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateProductSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(sampleProduct);

        ResponseEntity<?> response = productController.updateProduct(1L, sampleProduct, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleProduct, response.getBody());
    }

    @Test
    void testUpdateProductNotFound() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productService.updateProduct(eq(99L), any(Product.class))).thenReturn(null);

        ResponseEntity<?> response = productController.updateProduct(99L, sampleProduct, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_Success() {
        when(productService.deleteProduct(1L)).thenReturn(true);

        ResponseEntity<?> response = productController.deleteProduct(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productService.deleteProduct(99L)).thenReturn(false);

        ResponseEntity<?> response = productController.deleteProduct(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // =====================================================
    // === TESTS DE MÉTODOS CON PL/SQL =====================
    // =====================================================

    @Test
    void testCreateProductPLSQL_Success() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(productService).crearProductoProcedure(any(Product.class));

        ResponseEntity<?> response = productController.createProductPlSQL(sampleProduct, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateProductPLSQL_Error() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error PL/SQL")).when(productService).crearProductoProcedure(any(Product.class));

        ResponseEntity<?> response = productController.createProductPlSQL(sampleProduct, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetProductPLSQL_NotFound() throws Exception {
        when(productService.consultarProductoFunction(99L)).thenReturn(null);

        ResponseEntity<?> response = productController.getProductPLSQL(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
