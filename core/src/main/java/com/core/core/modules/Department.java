package com.core.core.modules;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DEPARTMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depID")
    private Long depID;

    @Column(name = "depName", nullable = false, unique = true, length = 100)
    private String depName;
}
