package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "CLIENT_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClientDetail {

    @Id
    @Column(name = "USERID")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USERID")
    @JsonBackReference
    private User user;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "SECONDNAME")
    private String secondName;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Column(name = "FIRSTLASTNAME", nullable = false)
    private String firstLastName;

    @Column(name = "SECONDLASTNAME")
    private String secondLastName;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "DESCADDRESS")
    private String descAddress;

    @NotNull(message = "La ciudad es obligatoria")
    @ManyToOne
    @JoinColumn(name = "CITYID", nullable = false)
    private City city;

    @NotNull(message = "El departamento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "DEPID", nullable = false)
    private Department department;
}
