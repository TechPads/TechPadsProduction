package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventoryClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inv_code", nullable = false)
    private Long invCode;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "inv_stock", nullable = false)
    private Integer invStock;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    @Digits(integer = 13, fraction = 2, message = "El precio tiene un formato inválido")
    @Column(name = "selling_price", nullable = false)
    private BigDecimal sellingPrice;

    @NotNull(message = "La fecha de inventario es obligatoria")
    @PastOrPresent(message = "La fecha de inventario no puede ser futura")
    @Column(name = "inv_date", nullable = false)
    private LocalDate invDate;

    @Column(name = "inv_status", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String status = "A";

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_code", nullable = false)
    private Product product;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inv_prov", nullable = false)
    private Provider provider;
}
