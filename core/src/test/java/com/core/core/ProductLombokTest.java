package com.core.core;

import com.core.core.modules.Product;
import com.core.core.modules.ProductType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductLombokTest {

    @Test
    public void testLombokGettersSetters() {
        // Crear objeto
        Product product = new Product();
        
        // Probar setters (generados por @Data)
        product.setProCode(1L);
        product.setProName("Test Product");
        product.setProPrice(BigDecimal.valueOf(100000));
        product.setProMark("Test Brand");
        
        // Probar getters (generados por @Data)
        assertEquals(1L, product.getProCode());
        assertEquals("Test Product", product.getProName());
        assertEquals(BigDecimal.valueOf(100000), product.getProPrice());
        assertEquals("Test Brand", product.getProMark());
        
        System.out.println(" Lombok funciona correctamente!");
    }
    
    @Test
    public void testLombokBuilder() {
        // Probar @Builder
        Product product = Product.builder()
                .proCode(2L)
                .proName("Builder Product")
                .proPrice(BigDecimal.valueOf(200000))
                .proMark("Builder Brand")
                .descript("Test description")
                .build();
        
        assertNotNull(product);
        assertEquals(2L, product.getProCode());
        
        System.out.println(" @Builder funciona correctamente!");
    }
    
    @Test
    public void testLombokConstructors() {
        // Probar @NoArgsConstructor
        Product product1 = new Product();
        assertNotNull(product1);
        
        // Probar @AllArgsConstructor
        Product product2 = new Product(
            3L, 
            "Constructor Product", 
            "image.jpg",
            BigDecimal.valueOf(300000),
            "Description",
            "Brand",
            null
        );
        
        assertEquals(3L, product2.getProCode());
        
        System.out.println(" Constructores funcionan correctamente!");
    }
}