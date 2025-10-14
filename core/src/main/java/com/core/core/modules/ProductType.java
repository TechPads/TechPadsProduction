package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "PRODUCTTYPE")
@Data
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TYPECODE")
    private Long typeCode;

    @Column(name = "TYPENAME", nullable = false, length = 100)
    private String typeName;

    public ProductType() {
    }

    public Long getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Long typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
