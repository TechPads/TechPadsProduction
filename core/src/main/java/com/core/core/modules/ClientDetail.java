package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "client_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientDetail {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Column(name = "first_last_name", nullable = false)
    private String firstLastName;

    @Column(name = "second_last_name")
    private String secondLastName;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "desc_address")
    private String descAddress;

    @NotNull(message = "La ciudad es obligatoria")
    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @NotNull(message = "El departamento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    private Department department;
}
