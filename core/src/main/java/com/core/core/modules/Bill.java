package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_code", nullable = false)
    private Long billCode;
    
    @NotNull(message = "El inventario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inv_code", nullable = false)
    private InventoryClass inventory;
    
    @NotNull(message = "La orden es obligatoria")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_id", nullable = false, unique = true)
    private Order order;
    
    @NotBlank(message = "El tipo de pago es obligatorio")
    @Pattern(regexp = "EFECTIVO|TARJETA|TRANSFERENCIA", 
             message = "El tipo de pago debe ser EFECTIVO, TARJETA o TRANSFERENCIA")
    @Column(name = "payment_type", length = 40, nullable = false)
    private String paymentType;
    
    @NotNull(message = "La fecha de la factura es obligatoria")
    @Temporal(TemporalType.DATE)
    @Column(name = "bill_date", nullable = false)
    private Date billDate;
    
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    @Column(name = "total_bill", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalBill;
    
    @PrePersist
    protected void onCreate() {
        if (billDate == null) {
            billDate = new Date();
        }
    }
}