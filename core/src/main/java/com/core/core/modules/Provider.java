package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provider")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Provider {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prov_id", nullable = false)
    private Long provId;
    
    @Column(name = "prov_email", nullable = false, length = 80, unique = true)
    private String provEmail;
    
    @Column(name = "prov_name", nullable = false, length = 80)
    private String provName;
    
    @Column(name = "prov_phone", nullable = false, length = 10)
    private String provPhone;
}