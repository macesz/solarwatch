package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.City;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
  Optional<City> findByNameAndCountryAndState(String name, String country, String state);

  Optional<City> findByNameAndCountry(String name, String country);
}
