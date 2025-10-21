package com.core.core.repository;

import com.core.core.modules.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByDepartment_DepID(Long depID);
}
