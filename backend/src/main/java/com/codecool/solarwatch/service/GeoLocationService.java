package com.codecool.solarwatch.service;

import com.codecool.solarwatch.client.GeoCodingApiClient;
import com.codecool.solarwatch.customErrorHandling.InvalidLocationException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.GeoLocation;
import com.codecool.solarwatch.repository.CityRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoLocationService {
  private static final String API_KEY = "3c4308a7a3e66d61d8e7f4b1cc5ec4bc";

  private final GeoCodingApiClient geoCodingApiClient;
  private final CityRepository cityRepository;
  private static final Logger logger = LoggerFactory.getLogger(GeoLocationService.class);

  @Autowired
  public GeoLocationService(GeoCodingApiClient geoCodingApiClient, CityRepository cityRepository) {
    this.geoCodingApiClient = geoCodingApiClient;
    this.cityRepository = cityRepository;
  }

  /**
   * Get city information, either from database or external API.
   *
   * @param city City name
   * @param countryCode Country code
   * @param stateCode State code (optional)
   * @return City entity with coordinates
   * @throws InvalidLocationException if city cannot be found
   */
  public City getCityInfo(String city, String countryCode, String stateCode) {
    Optional<City> cityOptional = findCityInDatabase(city, countryCode, stateCode);

    if (cityOptional.isPresent()) {
      logger.info("City found in database: {}", cityOptional.get().getName());
      return cityOptional.get();
    } else {
      logger.info("City not found in database, fetching from API: {}", city);
      return fetchAndSaveCity(city, countryCode, stateCode);
    }
  }

  /** Search for city in database. */
  private Optional<City> findCityInDatabase(String city, String countryCode, String stateCode) {
    if (stateCode != null) {
      return cityRepository.findByNameAndCountryAndState(city, countryCode, stateCode);
    } else {
      return cityRepository.findByNameAndCountry(city, countryCode);
    }
  }

  /** Fetch city data from external API and save to database. */
  private City fetchAndSaveCity(String city, String countryCode, String stateCode) {
    String s =
        stateCode != null
            ? city + ", " + countryCode + ", " + stateCode
            : city + ", " + countryCode;
    try {
      GeoLocation[] response =
          geoCodingApiClient.getGeoCoordinatesForCity(city, countryCode, stateCode);

      if (response == null || response.length == 0) {
        String locationString = s;
        throw new InvalidLocationException();
      }

      GeoLocation geoLocation = response[0];
      City newCity = createCityFromGeoLocation(geoLocation);

      logger.info("Saving new city to database: {}", newCity.getName());
      return cityRepository.save(newCity);

    } catch (Exception e) {
      if (e instanceof InvalidLocationException) {
        throw e;
      }
      String locationString = s;
      throw new InvalidLocationException();
    }
  }

  /** Convert GeoLocation to City entity. */
  private City createCityFromGeoLocation(GeoLocation geoLocation) {
    City newCity = new City();
    newCity.setName(geoLocation.name());
    newCity.setCountry(geoLocation.country());
    newCity.setState(geoLocation.state());
    newCity.setLatitude(geoLocation.lat());
    newCity.setLongitude(geoLocation.lon());
    return newCity;
  }
}
