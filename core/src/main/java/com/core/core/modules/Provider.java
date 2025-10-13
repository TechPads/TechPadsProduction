package com.core.core.modules;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROVIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROVID", nullable = false)
    private Long provId;

    @Column(name = "PROVEMAIL", nullable = false, length = 80, unique = true)
    private String provEmail;

    @Column(name = "PROVNAME", nullable = false, length = 80)
    private String provName;

    @Column(name = "PROVPHONE", nullable = false, length = 10)
    private String provPhone;
}
