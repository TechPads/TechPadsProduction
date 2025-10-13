package com.core.core.controllers;

import com.core.core.modules.ProductType;
import com.core.core.services.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/prodType")
public class ProductTypeController {

    @Autowired
    @Lazy
    private ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<List<ProductType>> getProductTypes() {
        List<ProductType> productTypes = productTypeService.getProductTypes();
        if (productTypes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productTypes);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getProductType(@PathVariable Long code) {
        ProductType productType = productTypeService.getProductType(code);
        if (productType == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tipo de producto no encontrado con código: " + code);
        }
        return ResponseEntity.ok(productType);
    }

    @PostMapping
    public ResponseEntity<?> postProductType(@RequestBody ProductType productType) {
        ProductType created = productTypeService.saveProductType(productType);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(created.getTypeCode())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> putProductType(
            @PathVariable Long code,
            @RequestBody ProductType productType
    ) {
        ProductType updated = productTypeService.updateProductType(code, productType);

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró tipo de producto con código: " + code);
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteProductType(@PathVariable Long code) {
        boolean deleted = productTypeService.deleteProductType(code);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró tipo de producto con código: " + code);
        }

        return ResponseEntity.noContent().build();
    }
}
