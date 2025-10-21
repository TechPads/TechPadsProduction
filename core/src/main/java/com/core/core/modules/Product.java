package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    
    @Id
    @Column(name = "PROCODE", nullable = false)
    private Long proCode;
    
    @Column(name = "PRONAME", nullable = false, length = 100)
    private String proName;
    
    @Column(name = "PROIMG", length = 500)
    private String proImg;
    
    @Column(name = "PROPRICE", nullable = false)
    private BigDecimal proPrice;
    
    @Column(name = "DESCRIPT", length = 1000)
    private String descript;
    
    @Column(name = "PROMARK", nullable = false, length = 100)
    private String proMark;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROTYPE", nullable = false)
    private ProductType productType;
}