package com.core.core.modules;

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

    @Column(name = "PROPRICE", nullable = false)
    private BigDecimal proPrice;

    @Column(name = "DESCRIPT", length = 1000)
    private String descript;

    @Column(name = "PROMARK", nullable = false, length = 100)
    private String proMark;

    // Relación con PRODUCTTYPE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROTYPE", nullable = false)
    private ProductType productType;

    public Product() {
    }

    //Setters & Getters


    public Long getProCode() {
        return proCode;
    }

    public void setProCode(Long proCode) {
        this.proCode = proCode;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProImg() {
        return proImg;
    }

    public void setProImg(String proImg) {
        this.proImg = proImg;
    }

    public BigDecimal getProPrice() {
        return proPrice;
    }

    public void setProPrice(BigDecimal proPrice) {
        this.proPrice = proPrice;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getProMark() {
        return proMark;
    }

    public void setProMark(String proMark) {
        this.proMark = proMark;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
