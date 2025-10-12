package com.core.core.controllers;

import com.core.core.modules.ProductType;
import com.core.core.services.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prodType")
public class ProductTypeController {

    @Autowired
    @Lazy
    private ProductTypeService productTypeService;

    @GetMapping
    public List<ProductType> getProductTypes() {
        return productTypeService.getProductTypes();
    }

    @GetMapping("/{code}")
    public ProductType getProductType(@PathVariable Long code) {
        return productTypeService.getProductType(code);
    }

    @PostMapping
    public ProductType addProductType(@RequestBody ProductType productType) {
        return productTypeService.saveProductType(productType);
    }

    @PutMapping("/{code}")
    public ProductType updateProductType(@PathVariable Long code, @RequestBody ProductType productType) {
        return productTypeService.updateProductType(code, productType);
    }

    @DeleteMapping("/{code}")
    public void deleteProductType(@PathVariable Long code) {
        productTypeService.deleteProductType(code);
    }
}
