package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "SECONDNAME")
    private String secondName;

    @Column(name = "FIRSTLASTNAME", nullable = false)
    private String firstLastName;

    @Column(name = "SECONDLASTNAME")
    private String secondLastName;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @Column(name = "DESCADDRESS")
    private String descAddress;

    @ManyToOne
    @JoinColumn(name = "CITYID", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "DEPID", nullable = false)
    private Department department;

}
