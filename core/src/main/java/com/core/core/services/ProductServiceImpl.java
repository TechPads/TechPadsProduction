package com.core.core.services;


import com.core.core.modules.Product;
import com.core.core.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    //Metodos normales con JPA
    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(Long code) {
        return productRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con código: " + code));
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long code, Product product) {
        Product existingProduct = productRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con codigo: " + code));

        existingProduct.setProName(product.getProName());
        existingProduct.setProPrice(product.getProPrice());
        existingProduct.setProImg(product.getProImg());
        existingProduct.setDescript(product.getDescript());
        existingProduct.setProMark(product.getProMark());
        existingProduct.setProductType(product.getProductType());

        return productRepository.save(existingProduct);
    }

    @Override
    public boolean deleteProduct(Long code) {
        Optional<Product> existing = productRepository.findById(code);
        if (existing.isPresent()) {
            productRepository.deleteById(code);
            return true;
        }
        return false;
    }

    //Metodos utilizando PL/SQL
    @Override
    public void crearProductoProcedure(Product product) {
        productRepository.crearProducto(
                product.getProCode(),
                product.getProName(),
                product.getProImg(),
                product.getProPrice(),
                product.getProductType().getTypeCode(),
                product.getDescript(),
                product.getProMark()
        );
    }

    public void modificarProductoProcedure(Long code, Product product) {
        productRepository.modificarProducto(
                code,
                product.getProName(),
                product.getProImg(),
                product.getProPrice(),
                product.getProductType().getTypeCode(),
                product.getDescript(),
                product.getProMark()
        );
    }

    @Override
    public void eliminarProductoProcedure(Long code) {
        productRepository.eliminarProducto(code);
    }

    @Override
    public Product consultarProductoFunction(Long code) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("GESTION_PRODUCTO.CONSULTAR_PRODUCTO", Product.class)
                .registerStoredProcedureParameter("v_proCode", Long.class, ParameterMode.IN)
                .setParameter("v_proCode", code);

        return (Product) query.getSingleResult();
    }

    @Override
    public List<Product> listarProductosProcedure() {
        // El procedure LISTAR_PRODUCTOS solo usa DBMS_OUTPUT,
        // por lo tanto retornamos todos los productos desde JPA.
        return productRepository.findAll();
    }

}
