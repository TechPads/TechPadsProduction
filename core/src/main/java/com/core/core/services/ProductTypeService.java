package com.core.core.services;

import com.core.core.modules.ProductType;
import java.util.List;
import java.util.Optional;

public interface ProductTypeService {

    List<ProductType> getProductTypes();

    ProductType getProductType(Long code);

    ProductType saveProductType(ProductType productType);

    ProductType updateProductType(Long code, ProductType productType);

    void deleteProductType(Long code);
}
