package com.core.core.modules;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INVENTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryClass {

    @Id
    @Column(name = "INVCODE", nullable = false)
    private Long invCode;

    @Column(name = "INVSTOCK", nullable = false)
    private Integer invStock;

    @Column(name = "SELLINGPRICE", nullable = false)
    private BigDecimal sellingPrice;

    @Column(name = "INVDATE", nullable = false)
    private LocalDate invDate;

    // Relación con PRODUCT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCODE", nullable = false)
    private Product product;

    // Relación con PROVIDER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVPROV", nullable = false)
    private Provider provider;
}
