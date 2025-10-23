package com.core.core.repository;

import com.core.core.modules.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Procedure(procedureName = "GESTION_PRODUCTO.CREAR_PRODUCTO")
    void crearProducto(
            @Param("v_proCode") Long proCode,
            @Param("v_proName") String proName,
            @Param("v_proImg") String proImg,
            @Param("v_proPrice") java.math.BigDecimal proPrice,
            @Param("v_proType") Long proType,
            @Param("v_descript") String descript,
            @Param("v_proMark") String proMark
    );

    @Procedure(procedureName = "GESTION_PRODUCTO.MODIFICAR_PRODUCTO")
    void modificarProducto(
            @Param("v_proCode") Long proCode,
            @Param("v_proName") String proName,
            @Param("v_proImg") String proImg,
            @Param("v_proPrice") java.math.BigDecimal proPrice,
            @Param("v_proType") Long proType,
            @Param("v_descript") String descript,
            @Param("v_proMark") String proMark
    );


    @Procedure(procedureName = "GESTION_PRODUCTO.ELIMINAR_PRODUCTO")
    void eliminarProducto(@Param("v_proCode") Long proCode);
}
