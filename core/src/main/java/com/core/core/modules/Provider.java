package com.core.core.modules;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROVIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROVID", nullable = false)
    private Long provId;

    @Column(name = "PROVEMAIL", nullable = false, length = 80, unique = true)
    private String provEmail;

    @Column(name = "PROVNAME", nullable = false, length = 80)
    private String provName;

    @Column(name = "PROVPHONE", nullable = false, length = 10)
    private String provPhone;

    public Provider() {
    }

    public Long getProvId() {
        return provId;
    }

    public void setProvId(Long provId) {
        this.provId = provId;
    }

    public String getProvEmail() {
        return provEmail;
    }

    public void setProvEmail(String provEmail) {
        this.provEmail = provEmail;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getProvPhone() {
        return provPhone;
    }

    public void setProvPhone(String provPhone) {
        this.provPhone = provPhone;
    }
}
