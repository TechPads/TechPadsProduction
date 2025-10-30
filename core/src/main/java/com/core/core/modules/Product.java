package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROCODE", nullable = false)
    private Long proCode;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no puede tener más de 100 caracteres")
    @Column(name = "PRONAME", nullable = false, length = 100)
    private String proName;

    @Size(max = 500, message = "La URL de la imagen no puede tener más de 500 caracteres")
    @Column(name = "PROIMG", length = 500)
    private String proImg;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 13, fraction = 2, message = "El precio tiene un formato inválido")
    @Column(name = "PROPRICE", nullable = false)
    private BigDecimal proPrice;

    @Size(max = 1000, message = "La descripción no puede tener más de 1000 caracteres")
    @Column(name = "DESCRIPT", length = 1000)
    private String descript;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100, message = "La marca no puede tener más de 100 caracteres")
    @Column(name = "PROMARK", nullable = false, length = 100)
    private String proMark;

    @NotBlank(message = "El estado es obligatorio")
    @Column(name = "PROSTATUS", nullable = false, length = 1, columnDefinition = "CHAR(1) DEFAULT 'A'")
    private String status;

    @NotNull(message = "El tipo de producto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROTYPE", nullable = false)
    private ProductType productType;
}
