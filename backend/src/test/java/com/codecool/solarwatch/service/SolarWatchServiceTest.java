package com.codecool.solarwatch.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.codecool.solarwatch.client.SunriseSunsetApiClient;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SolarWatchReport;
import com.codecool.solarwatch.model.SolarWatchResult;
import com.codecool.solarwatch.model.SunsetSunrise;
import com.codecool.solarwatch.repository.SunsetSunriseRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolarWatchServiceTest {

  @Mock private SunsetSunriseRepository sunsetSunriseRepository;
  @Mock private SunriseSunsetApiClient sunriseSunsetApiClient;

  private SolarWatchService solarWatchService;

  @BeforeEach
  void setUp() {
    solarWatchService = new SolarWatchService(sunriseSunsetApiClient, sunsetSunriseRepository);
  }

  @Test
  @DisplayName("Should return report from database when it exists")
  void getReport_whenDataExistsInDb_shouldReturnReport() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    SunsetSunrise storedData = new SunsetSunrise();
    storedData.setCity(city);
    storedData.setDate(date);
    storedData.setSunrise("06:00:00 AM");
    storedData.setSunset("08:00:00 PM");
    storedData.setStatus("OK");

    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.of(storedData));

    // Act
    SolarWatchReport result = solarWatchService.getReport(city, date);

    // Assert
    assertNotNull(result);
    assertEquals("OK", result.status());
    assertEquals("06:00:00 AM", result.results().sunrise());
    assertEquals("08:00:00 PM", result.results().sunset());

    verify(sunsetSunriseRepository).findByCityIdAndDate(city.getId(), date);
    verifyNoInteractions(sunriseSunsetApiClient); // API should not be called
  }

  @Test
  @DisplayName("Should fetch report from API when not found in database")
  void getReport_whenDataNotInDb_shouldFetchFromApiAndSave() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API response
    SolarWatchResult apiResultData = new SolarWatchResult("05:30:00 AM", "07:45:00 PM");
    SolarWatchReport apiResponse = new SolarWatchReport("OK", apiResultData);

    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
            city.getLatitude(), city.getLongitude(), date))
        .thenReturn(apiResponse);

    // Act
    SolarWatchReport result = solarWatchService.getReport(city, date);

    // Assert
    assertNotNull(result);
    assertEquals("OK", result.status());
    assertEquals("05:30:00 AM", result.results().sunrise());
    assertEquals("07:45:00 PM", result.results().sunset());

    verify(sunsetSunriseRepository).findByCityIdAndDate(city.getId(), date);
    verify(sunriseSunsetApiClient)
        .getSunriseSunsetByCoordinates(city.getLatitude(), city.getLongitude(), date);
    verify(sunsetSunriseRepository).save(any(SunsetSunrise.class));
  }

  @Test
  @DisplayName("Should verify correct SunsetSunrise entity is saved after API fetch")
  void getReport_whenSavingFromApi_shouldSaveCorrectEntity() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API response
    SolarWatchResult apiResultData = new SolarWatchResult("05:30:00 AM", "07:45:00 PM");
    SolarWatchReport apiResponse = new SolarWatchReport("OK", apiResultData);

    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
            city.getLatitude(), city.getLongitude(), date))
        .thenReturn(apiResponse);

    // Act
    SolarWatchReport result = solarWatchService.getReport(city, date);

    // Assert
    assertNotNull(result);

    // Capture the saved entity and verify its properties
    ArgumentCaptor<SunsetSunrise> sunsetSunriseCaptor =
        ArgumentCaptor.forClass(SunsetSunrise.class);
    verify(sunsetSunriseRepository).save(sunsetSunriseCaptor.capture());

    SunsetSunrise savedEntity = sunsetSunriseCaptor.getValue();
    assertEquals(city, savedEntity.getCity());
    assertEquals(date, savedEntity.getDate());
    assertEquals("05:30:00 AM", savedEntity.getSunrise());
    assertEquals("07:45:00 PM", savedEntity.getSunset());
    assertEquals("OK", savedEntity.getStatus());
  }

  @Test
  @DisplayName("Should call API with correct coordinates and date")
  void getReport_whenFetchingFromApi_shouldUseCorrectParameters() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API response
    SolarWatchResult apiResultData = new SolarWatchResult("06:00:00 AM", "08:00:00 PM");
    SolarWatchReport apiResponse = new SolarWatchReport("OK", apiResultData);

    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(47.5, 19.0, date))
        .thenReturn(apiResponse);

    // Act
    solarWatchService.getReport(city, date);

    // Assert
    verify(sunriseSunsetApiClient).getSunriseSunsetByCoordinates(47.5, 19.0, date);
  }

  @Test
  @DisplayName("Should handle API errors gracefully")
  void getReport_whenApiThrowsError_shouldHandleGracefully() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API throwing exception
    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
            city.getLatitude(), city.getLongitude(), date))
        .thenThrow(new RuntimeException("API connection failed"));

    // Act & Assert
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> solarWatchService.getReport(city, date));

    assertEquals("Failed to fetch sunrise/sunset data", exception.getMessage());

    verify(sunsetSunriseRepository).findByCityIdAndDate(city.getId(), date);
    verify(sunriseSunsetApiClient)
        .getSunriseSunsetByCoordinates(city.getLatitude(), city.getLongitude(), date);
    verify(sunsetSunriseRepository, never()).save(any(SunsetSunrise.class));
  }

  @Test
  @DisplayName("Should handle null API response gracefully")
  void getReport_whenApiReturnsNull_shouldHandleGracefully() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API returning null
    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
            city.getLatitude(), city.getLongitude(), date))
        .thenReturn(null);

    // Act
    SolarWatchReport result = solarWatchService.getReport(city, date);

    // Assert
    assertNull(result);

    verify(sunsetSunriseRepository).findByCityIdAndDate(city.getId(), date);
    verify(sunriseSunsetApiClient)
        .getSunriseSunsetByCoordinates(city.getLatitude(), city.getLongitude(), date);
    verify(sunsetSunriseRepository, never()).save(any(SunsetSunrise.class));
  }

  @Test
  @DisplayName("Should handle API response with null results")
  void getReport_whenApiResponseHasNullResults_shouldNotSaveToDatabase() {
    // Arrange
    City city = createTestCity();
    LocalDate date = LocalDate.of(2023, 5, 15);

    // Data not in DB
    when(sunsetSunriseRepository.findByCityIdAndDate(city.getId(), date))
        .thenReturn(Optional.empty());

    // Mock API response with null results
    SolarWatchReport apiResponse = new SolarWatchReport("OK", null);

    when(sunriseSunsetApiClient.getSunriseSunsetByCoordinates(
            city.getLatitude(), city.getLongitude(), date))
        .thenReturn(apiResponse);

    // Act
    SolarWatchReport result = solarWatchService.getReport(city, date);

    // Assert
    assertNotNull(result);
    assertEquals("OK", result.status());
    assertNull(result.results());

    verify(sunsetSunriseRepository).findByCityIdAndDate(city.getId(), date);
    verify(sunriseSunsetApiClient)
        .getSunriseSunsetByCoordinates(city.getLatitude(), city.getLongitude(), date);
    verify(sunsetSunriseRepository, never()).save(any(SunsetSunrise.class));
  }

  private City createTestCity() {
    City city = new City();
    city.setId(1L);
    city.setName("Budapest");
    city.setCountry("HU");
    city.setLatitude(47.5);
    city.setLongitude(19.0);
    return city;
  }
}
