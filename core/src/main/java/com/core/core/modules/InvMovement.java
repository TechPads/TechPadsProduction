package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "inv_movement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inv_mov_id", nullable = false)
    private Long invMovID;
    
    @NotNull(message = "El inventario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inv_code", nullable = false)
    private InventoryClass inventory;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "ENTRADA|SALIDA|DEVOLUCION|AJUSTE",
             message = "El tipo debe ser ENTRADA, SALIDA, DEVOLUCION o AJUSTE")
    @Column(name = "mov_type", length = 20, nullable = false)
    private String movType;
    
    @NotNull(message = "La fecha del movimiento es obligatoria")
    @Temporal(TemporalType.DATE)
    @Column(name = "mov_date", nullable = false)
    private Date movDate;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "prev_stock")
    private Integer prevStock;
    
    @Column(name = "new_stock")
    private Integer newStock;
    
    @Column(name = "reason", length = 200)
    private String reason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_id")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    protected void onCreate() {
        if (movDate == null) {
            movDate = new Date();
        }
    }
}