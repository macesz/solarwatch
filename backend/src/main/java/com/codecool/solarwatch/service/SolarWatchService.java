package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SolarWatchReport;
import com.codecool.solarwatch.model.SolarWatchResult;
import com.codecool.solarwatch.model.SunsetSunrise;
import com.codecool.solarwatch.repository.SunsetSunriseRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolarWatchService {
  //  private static final String API_KEY = "<KEY>";

  private final SunriseSunsetApiClient sunriseSunsetApiClient;
  private final SunsetSunriseRepository sunsetSunriseRepository;
  private static final Logger logger = LoggerFactory.getLogger(SolarWatchService.class);

  @Autowired
  public SolarWatchService(
      SunriseSunsetApiClient sunriseSunsetApiClient,
      SunsetSunriseRepository sunsetSunriseRepository) {
    this.sunriseSunsetApiClient = sunriseSunsetApiClient;
    this.sunsetSunriseRepository = sunsetSunriseRepository;
  }

  /**
   * Get sunrise/sunset report for a city on a specific date. Searches database first, then calls
   * external API if needed.
   *
   * @param city City entity with coordinates
   * @param date Date for sunrise/sunset data
   * @return SolarWatchReport containing sunrise/sunset times
   */
  public SolarWatchReport getReport(City city, LocalDate date) {
    Optional<SunsetSunrise> existingData =
        sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date);

    if (existingData.isPresent()) {
      logger.info("Found existing sunrise/sunset data for {} on {}", city.getName(), date);
      return convertToSolarWatchReport(existingData.get());
    } else {
      logger.info("No existing data found, fetching from API for {} on {}", city.getName(), date);
      return fetchAndSaveSunsetSunrise(city, date);
    }
  }

  /** Fetch sunrise/sunset data from external API and save to database. */
  private SolarWatchReport fetchAndSaveSunsetSunrise(City city, LocalDate date) {
    try {
      SolarWatchReport response =
          sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
              city.getLatitude(), city.getLongitude(), date);

      if (response != null && response.results() != null) {
        saveSunsetSunriseToDatabase(city, date, response);
        logger.info("Successfully saved sunrise/sunset data for {}", city.getName());
      }

      return response;

    } catch (Exception e) {
      logger.error(
          "Error fetching sunrise/sunset data for {} on {}: {}",
          city.getName(),
          date,
          e.getMessage());
      throw new RuntimeException("Failed to fetch sunrise/sunset data", e);
    }
  }

  /** Save sunrise/sunset data to database. */
  private void saveSunsetSunriseToDatabase(City city, LocalDate date, SolarWatchReport response) {
    SunsetSunrise sunsetSunrise = new SunsetSunrise();
    sunsetSunrise.setCity(city);
    sunsetSunrise.setDate(date);
    sunsetSunrise.setSunrise(response.results().sunrise());
    sunsetSunrise.setSunset(response.results().sunset());
    sunsetSunrise.setStatus(response.status());

    sunsetSunriseRepository.save(sunsetSunrise);
  }

  /** Convert SunsetSunrise entity to SolarWatchReport. */
  private SolarWatchReport convertToSolarWatchReport(SunsetSunrise sunsetSunrise) {
    SolarWatchResult result =
        new SolarWatchResult(sunsetSunrise.getSunrise(), sunsetSunrise.getSunset());
    return new SolarWatchReport(sunsetSunrise.getStatus(), result);
  }
}
