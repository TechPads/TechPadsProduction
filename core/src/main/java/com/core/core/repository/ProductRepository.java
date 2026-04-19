package com.core.core.repository;

import com.core.core.modules.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================
    // FUNCIONES POSTGRESQL
    // =========================

    @Modifying
    @Transactional
    @Query(value = "SELECT crear_producto(:proName, :proImg, :proPrice, :proType, :descript, :proMark, :status)", nativeQuery = true)
    void crearProducto(
            @Param("proName") String proName,
            @Param("proImg") String proImg,
            @Param("proPrice") BigDecimal proPrice,
            @Param("proType") String proType,   // ⚠️ CAMBIÓ A TEXT
            @Param("descript") String descript,
            @Param("proMark") String proMark,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = "SELECT modificar_producto(:proCode, :proName, :proImg, :proPrice, :proType, :descript, :proMark, :status)", nativeQuery = true)
    void modificarProducto(
            @Param("proCode") Long proCode,
            @Param("proName") String proName,
            @Param("proImg") String proImg,
            @Param("proPrice") BigDecimal proPrice,
            @Param("proType") String proType,   // ⚠️ CAMBIÓ A TEXT
            @Param("descript") String descript,
            @Param("proMark") String proMark,
            @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = "SELECT eliminar_producto(:proCode)", nativeQuery = true)
    void eliminarProducto(@Param("proCode") Long proCode);


    // =========================
    // CONSULTAS NORMALES
    // =========================

    @Query("SELECT p FROM Product p WHERE LOWER(p.proName) LIKE LOWER(CONCAT(:text, '%'))")
    List<Product> searchByName(@Param("text") String text);
}