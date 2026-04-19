package com.core.core.modules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Long cityID;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "dep_id", nullable = false)
    private Department department;

}
