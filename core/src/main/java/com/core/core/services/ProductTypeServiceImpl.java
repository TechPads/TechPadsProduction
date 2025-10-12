package com.core.core.services;

import com.core.core.modules.ProductType;
import com.core.core.repository.ProductTypeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
public class ProductTypeServiceImpl implements ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    @Override
    public List<ProductType> getProductTypes(){
        return productTypeRepository.findAll();
    }

    @Override
    public ProductType getProductType(Long code){
        return productTypeRepository.findById(code).orElse(null);
    }

    @Override
    public ProductType saveProductType(ProductType productType){
        return productTypeRepository.save(productType);
    }

    @Override
    public ProductType updateProductType(Long code, ProductType productTypeDetails) {
        return productTypeRepository.findById(code)
                .map(existingType -> {
                    existingType.setTypeName(productTypeDetails.getTypeName());
                    return productTypeRepository.save(existingType);
                })
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de producto no encontrado con código " + code));
    }

    public void deleteProductType(Long code){
        productTypeRepository.deleteById(code);
    }
}

