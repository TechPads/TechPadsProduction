package com.core.core.module;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @Column(name = "PROCODE", nullable = false)
    private Long proCode;

    @Column(name = "PRONAME", nullable = false, length = 100)
    private String proName;

    @Column(name = "PROIMG", length = 500)
    private String proImg;

    @Column(name = "PROPRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal proPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROTYPE", nullable = false)
    private ProductType productType;

    @Column(name = "DESCRIPT", nullable = false, length = 1000)
    private String descript;

    @Column(name = "PROMARK", nullable = false, length = 100)
    private String proMark;
}
