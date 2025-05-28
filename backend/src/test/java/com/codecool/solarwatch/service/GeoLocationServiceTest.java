package com.codecool.solarwatch.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.customErrorHandling.InvalidLocationException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.GeoLocation;
import com.codecool.solarwatch.repository.CityRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeoLocationServiceTest {

  @Mock private CityRepository cityRepository;
  @Mock private GeoCodingApiClient geoCodingApiClient;

  private GeoLocationService geoLocationService;

  @BeforeEach
  void setUp() {
    geoLocationService = new GeoLocationService(geoCodingApiClient, cityRepository);
  }

  @Test
  @DisplayName("Should return city from database when it exists (without state)")
  void getCityInfo_whenCityExistsInDb_shouldReturnCity() {
    // Arrange
    String cityName = "Budapest";
    String countryCode = "HU";
    String stateCode = null;

    City expectedCity = new City();
    expectedCity.setName(cityName);
    expectedCity.setCountry(countryCode);
    expectedCity.setLatitude(47.4979);
    expectedCity.setLongitude(19.0402);

    when(cityRepository.findByNameAndCountry(cityName, countryCode))
        .thenReturn(Optional.of(expectedCity));

    // Act
    City result = geoLocationService.getCityInfo(cityName, countryCode, stateCode);

    // Assert
    assertNotNull(result);
    assertEquals(cityName, result.getName());
    assertEquals(countryCode, result.getCountry());
    assertEquals(47.4979, result.getLatitude());

    verify(cityRepository).findByNameAndCountry(cityName, countryCode);
    verifyNoInteractions(geoCodingApiClient); // API should not be called
  }

  @Test
  @DisplayName("Should return city from database when it exists (with state)")
  void getCityInfo_whenCityWithStateExistsInDb_shouldReturnCity() {
    // Arrange
    String cityName = "Austin";
    String countryCode = "US";
    String stateCode = "TX";

    City expectedCity = new City();
    expectedCity.setName(cityName);
    expectedCity.setCountry(countryCode);
    expectedCity.setState(stateCode);
    expectedCity.setLatitude(30.2672);
    expectedCity.setLongitude(-97.7431);

    when(cityRepository.findByNameAndCountryAndState(cityName, countryCode, stateCode))
        .thenReturn(Optional.of(expectedCity));

    // Act
    City result = geoLocationService.getCityInfo(cityName, countryCode, stateCode);

    // Assert
    assertNotNull(result);
    assertEquals(cityName, result.getName());
    assertEquals(countryCode, result.getCountry());
    assertEquals(stateCode, result.getState());
    assertEquals(30.2672, result.getLatitude());

    verify(cityRepository).findByNameAndCountryAndState(cityName, countryCode, stateCode);
    verifyNoInteractions(geoCodingApiClient); // API should not be called
  }

  @Test
  @DisplayName("Should fetch city from API when not found in database")
  void getCityInfo_whenCityNotInDb_shouldFetchFromApiAndSave() {
    // Arrange
    String cityName = "Paris";
    String countryCode = "FR";
    String stateCode = null;

    // City not in DB
    when(cityRepository.findByNameAndCountry(cityName, countryCode)).thenReturn(Optional.empty());

    // Mock API response
    GeoLocation[] apiResponse =
        new GeoLocation[] {new GeoLocation(cityName, 48.8566f, 2.3522f, countryCode, null)};
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenReturn(apiResponse);

    // Mock saving to repository
    City savedCity = new City();
    savedCity.setId(1L);
    savedCity.setName(cityName);
    savedCity.setCountry(countryCode);
    savedCity.setLatitude(48.8566);
    savedCity.setLongitude(2.3522);

    when(cityRepository.save(any(City.class))).thenReturn(savedCity);

    // Act
    City result = geoLocationService.getCityInfo(cityName, countryCode, stateCode);

    // Assert
    assertNotNull(result);
    assertEquals(cityName, result.getName());
    assertEquals(countryCode, result.getCountry());
    assertEquals(48.8566, result.getLatitude());

    verify(cityRepository).findByNameAndCountry(cityName, countryCode);
    verify(geoCodingApiClient).getGeoCoordinatesForCity(cityName, countryCode, stateCode);
    verify(cityRepository).save(any(City.class));
  }

  @Test
  @DisplayName("Should fetch city with state from API when not found in database")
  void getCityInfo_whenCityWithStateNotInDb_shouldFetchFromApiAndSave() {
    // Arrange
    String cityName = "Springfield";
    String countryCode = "US";
    String stateCode = "IL";

    // City not in DB
    when(cityRepository.findByNameAndCountryAndState(cityName, countryCode, stateCode))
        .thenReturn(Optional.empty());

    // Mock API response
    GeoLocation[] apiResponse =
        new GeoLocation[] {new GeoLocation(cityName, 39.7817f, -89.6501f, countryCode, stateCode)};
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenReturn(apiResponse);

    // Mock saving to repository
    City savedCity = new City();
    savedCity.setId(2L);
    savedCity.setName(cityName);
    savedCity.setCountry(countryCode);
    savedCity.setState(stateCode);
    savedCity.setLatitude(39.7817);
    savedCity.setLongitude(-89.6501);

    when(cityRepository.save(any(City.class))).thenReturn(savedCity);

    // Act
    City result = geoLocationService.getCityInfo(cityName, countryCode, stateCode);

    // Assert
    assertNotNull(result);
    assertEquals(cityName, result.getName());
    assertEquals(countryCode, result.getCountry());
    assertEquals(stateCode, result.getState());
    assertEquals(39.7817, result.getLatitude());

    verify(cityRepository).findByNameAndCountryAndState(cityName, countryCode, stateCode);
    verify(geoCodingApiClient).getGeoCoordinatesForCity(cityName, countryCode, stateCode);
    verify(cityRepository).save(any(City.class));
  }

  @Test
  @DisplayName("Should throw InvalidLocationException when API returns no results")
  void getCityInfo_whenApiReturnsNoResults_shouldThrowException() {
    // Arrange
    String cityName = "NonExistentCity";
    String countryCode = "XX";
    String stateCode = null;

    // City not in DB
    when(cityRepository.findByNameAndCountry(cityName, countryCode)).thenReturn(Optional.empty());

    // Mock API returning empty array
    GeoLocation[] emptyResponse = new GeoLocation[0];
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenReturn(emptyResponse);

    // Act & Assert
    InvalidLocationException exception =
        assertThrows(
            InvalidLocationException.class,
            () -> geoLocationService.getCityInfo(cityName, countryCode, stateCode));

    assertEquals("Location not valid", exception.getMessage());

    verify(cityRepository).findByNameAndCountry(cityName, countryCode);
    verify(geoCodingApiClient).getGeoCoordinatesForCity(cityName, countryCode, stateCode);
    verify(cityRepository, never()).save(any(City.class));
  }

  @Test
  @DisplayName("Should throw InvalidLocationException when API returns null")
  void getCityInfo_whenApiReturnsNull_shouldThrowException() {
    // Arrange
    String cityName = "InvalidCity";
    String countryCode = "YY";
    String stateCode = "ZZ";

    // City not in DB
    when(cityRepository.findByNameAndCountryAndState(cityName, countryCode, stateCode))
        .thenReturn(Optional.empty());

    // Mock API returning null
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenReturn(null);

    // Act & Assert
    InvalidLocationException exception =
        assertThrows(
            InvalidLocationException.class,
            () -> geoLocationService.getCityInfo(cityName, countryCode, stateCode));

    assertEquals("Location not valid", exception.getMessage());

    verify(cityRepository).findByNameAndCountryAndState(cityName, countryCode, stateCode);
    verify(geoCodingApiClient).getGeoCoordinatesForCity(cityName, countryCode, stateCode);
    verify(cityRepository, never()).save(any(City.class));
  }

  @Test
  @DisplayName("Should throw InvalidLocationException when API throws exception")
  void getCityInfo_whenApiThrowsError_shouldThrowInvalidLocationException() {
    // Arrange
    String cityName = "Berlin";
    String countryCode = "DE";
    String stateCode = null;

    // City not in DB
    when(cityRepository.findByNameAndCountry(cityName, countryCode)).thenReturn(Optional.empty());

    // Mock API throwing exception
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenThrow(new RuntimeException("API connection failed"));

    // Act & Assert
    InvalidLocationException exception =
        assertThrows(
            InvalidLocationException.class,
            () -> geoLocationService.getCityInfo(cityName, countryCode, stateCode));

    assertEquals("Location not valid", exception.getMessage());

    verify(cityRepository).findByNameAndCountry(cityName, countryCode);
    verify(geoCodingApiClient).getGeoCoordinatesForCity(cityName, countryCode, stateCode);
    verify(cityRepository, never()).save(any(City.class));
  }

  @Test
  @DisplayName("Should properly create City from GeoLocation data")
  void getCityInfo_shouldCreateCityWithCorrectData() {
    // Arrange
    String cityName = "Tokyo";
    String countryCode = "JP";
    String stateCode = null;

    when(cityRepository.findByNameAndCountry(cityName, countryCode)).thenReturn(Optional.empty());

    GeoLocation geoLocation = new GeoLocation("Tokyo", 35.6762f, 139.6503f, "JP", "Tokyo");
    when(geoCodingApiClient.getGeoCoordinatesForCity(cityName, countryCode, stateCode))
        .thenReturn(new GeoLocation[] {geoLocation});

    City savedCity = new City();
    savedCity.setId(3L);
    savedCity.setName("Tokyo");
    savedCity.setCountry("JP");
    savedCity.setState("Tokyo");
    savedCity.setLatitude(35.6762);
    savedCity.setLongitude(139.6503);

    when(cityRepository.save(any(City.class))).thenReturn(savedCity);

    // Act
    City result = geoLocationService.getCityInfo(cityName, countryCode, stateCode);

    // Assert
    assertNotNull(result);
    assertEquals("Tokyo", result.getName());
    assertEquals("JP", result.getCountry());
    assertEquals("Tokyo", result.getState());
    assertEquals(35.6762, result.getLatitude());
    assertEquals(139.6503, result.getLongitude());

    // Capture the actual argument passed to save() and verify its properties
    ArgumentCaptor<City> cityCaptor = ArgumentCaptor.forClass(City.class);
    verify(cityRepository).save(cityCaptor.capture());

    City capturedCity = cityCaptor.getValue();
    assertEquals("Tokyo", capturedCity.getName());
    assertEquals("JP", capturedCity.getCountry());
    assertEquals("Tokyo", capturedCity.getState());
    assertEquals(35.6762, capturedCity.getLatitude(), 0.0001); // Use delta for double comparison
    assertEquals(139.6503, capturedCity.getLongitude(), 0.0001); // Use delta for double comparison
  }
}
