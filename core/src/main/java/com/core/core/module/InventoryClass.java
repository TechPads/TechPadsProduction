package com.core.core.module;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @Column(name = "SELLINGPRICE", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCODE", nullable = false)
    private Product product;
}
