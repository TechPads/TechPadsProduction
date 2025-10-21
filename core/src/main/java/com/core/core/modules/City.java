package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CITIES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CITYID")
    private Long cityID;

    @Column(name = "CITYNAME", nullable = false, length = 100)
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "DEPID", nullable = false)
    private Department department;

}
