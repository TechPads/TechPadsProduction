package com.core.core.repository;

import com.core.core.modules.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

        List<Cart> findByUser_Id(Long userId);

        Optional<Cart> findByUser_IdAndProCode_ProCode(Long userId, Long proCode);

        @Modifying
        @Transactional
        @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
        void deleteByUser_Id(@Param("userId") Long userId);

        // =========================
        // FUNCIONES POSTGRESQL
        // =========================

        @Transactional
        @Query(value = "SELECT agregar_al_carrito(:userID, :proCode, :quantity)", nativeQuery = true)
        Object agregarAlCarrito(@Param("userID") Long userID,
                                @Param("proCode") Long proCode,
                                @Param("quantity") Integer quantity);

        @Transactional
        @Query(value = "SELECT actualizar_cantidad(:cartID, :quantity)", nativeQuery = true)
        Object actualizarCantidad(@Param("cartID") Long cartID,
                                  @Param("quantity") Integer quantity);

        @Transactional
        @Query(value = "SELECT eliminar_del_carrito(:cartID)", nativeQuery = true)
        Object eliminarDelCarrito(@Param("cartID") Long cartID);

        @Transactional
        @Query(value = "SELECT limpiar_carrito(:userID)", nativeQuery = true)
        Object limpiarCarrito(@Param("userID") Long userID);

        @Query(value = "SELECT calcular_total_carrito(:userID)", nativeQuery = true)
        Double calcularTotalCarrito(@Param("userID") Long userID);

        @Query(value = "SELECT verificar_disponibilidad_carrito(:userID)", nativeQuery = true)
        Boolean verificarDisponibilidadCarrito(@Param("userID") Long userID);

        @Query(value = "SELECT procesar_compra(:userId, :paymentType)", nativeQuery = true)
        Long procesarCompra(@Param("userId") Long userId,
                            @Param("paymentType") String paymentType);
}