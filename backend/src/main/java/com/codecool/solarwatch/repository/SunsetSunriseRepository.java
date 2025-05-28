package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.SunsetSunrise;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SunsetSunriseRepository extends JpaRepository<SunsetSunrise, Long> {
  Optional<SunsetSunrise> findByCityIdAndDate(Long cityId, LocalDate date);
}
