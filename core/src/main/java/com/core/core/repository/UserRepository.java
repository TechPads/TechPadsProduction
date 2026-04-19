package com.core.core.repository;

import com.core.core.modules.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByStatus(String status);

    // =========================
    // POSTGRESQL FUNCTIONS
    // =========================

    @Modifying
    @Transactional
    @Query(value = """
        SELECT registrar_usuario(
            :userName,
            :password,
            :email,
            :phone,
            :firstName,
            :secondName,
            :firstLastName,
            :secondLastName,
            :address,
            :descAddress,
            :cityId,
            :depId
        )
    """, nativeQuery = true)
    void registrarUsuario(
            @Param("userName") String userName,
            @Param("password") String password,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("firstName") String firstName,
            @Param("secondName") String secondName,
            @Param("firstLastName") String firstLastName,
            @Param("secondLastName") String secondLastName,
            @Param("address") String address,
            @Param("descAddress") String descAddress,
            @Param("cityId") Long cityId,
            @Param("depId") Long depId
    );

    @Modifying
    @Transactional
    @Query(value = """
        SELECT modificar_usuario(
            :userId,
            :phone,
            :email,
            :password,
            :firstName,
            :secondName,
            :firstLastName,
            :secondLastName,
            :address,
            :descAddress,
            :cityId,
            :depId
        )
    """, nativeQuery = true)
    void modificarUsuario(
            @Param("userId") Long userId,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("password") String password,
            @Param("firstName") String firstName,
            @Param("secondName") String secondName,
            @Param("firstLastName") String firstLastName,
            @Param("secondLastName") String secondLastName,
            @Param("address") String address,
            @Param("descAddress") String descAddress,
            @Param("cityId") Long cityId,
            @Param("depId") Long depId
    );
}