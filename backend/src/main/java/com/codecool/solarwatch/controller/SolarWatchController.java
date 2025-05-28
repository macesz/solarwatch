package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.customErrorHandling.InvalidLocationException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SolarWatchReport;
import com.codecool.solarwatch.service.GeoLocationService;
import com.codecool.solarwatch.service.SolarWatchService;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class SolarWatchController {

  private static final Logger logger = LoggerFactory.getLogger(SolarWatchController.class);
  private final SolarWatchService solarWatchService;
  private final GeoLocationService locationService;

  public SolarWatchController(
      SolarWatchService solarWatchService, GeoLocationService geoLocationService) {
    this.solarWatchService = solarWatchService;
    this.locationService = geoLocationService;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/sunset-sunrise")
  public ResponseEntity<Object> getData(
      @RequestParam String city,
      @RequestParam String countryCode,
      @RequestParam(required = false) String stateCode,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    try {
      // Manual validation
      if (city == null || city.trim().isEmpty()) {
        throw new IllegalArgumentException("City cannot be empty");
      }

      if (countryCode == null || countryCode.trim().isEmpty()) {
        throw new IllegalArgumentException("Country code cannot be empty");
      }

      City cityInfo = locationService.getCityInfo(city, countryCode, stateCode);
      SolarWatchReport report = solarWatchService.getReport(cityInfo, date);
      return ResponseEntity.ok(report);

    } catch (InvalidLocationException e) {
      logger.error("Invalid location: {}", e.getMessage());
      return ResponseEntity.badRequest().body("Location not valid");
    } catch (IllegalArgumentException e) {
      logger.error("Validation error: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
      logger.error("Service error: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Internal server error occurred");
    }
  }
}
