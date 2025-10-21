package com.core.core.services;

import com.core.core.modules.City;

import java.util.List;

public interface CityService {

    List<City> getCities();
    City getCity(Long id);
    List<City> getCitiesByDepartment(Long depId);
}
