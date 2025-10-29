package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INVENTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventoryClass {

    @Id
    @Column(name = "INVCODE", nullable = false)
    @Positive(message = "El código de inventario debe ser un número positivo")
    private Long invCode;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "INVSTOCK", nullable = false)
    private Integer invStock;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    @Column(name = "SELLINGPRICE", nullable = false)
    private BigDecimal sellingPrice;

    @NotNull(message = "La fecha de inventario es obligatoria")
    @PastOrPresent(message = "La fecha de inventario no puede ser futura")
    @Column(name = "INVDATE", nullable = false)
    private LocalDate invDate;

    // Relación con PRODUCT
    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCODE", nullable = false)
    private Product product;

    // Relación con PROVIDER
    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVPROV", nullable = false)
    private Provider provider;
}
