package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.GeoLocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeoCodingApiClient {

  private final WebClient webClient;
  private final String apiKey;
  private final String baseUrl;

  public GeoCodingApiClient(
      WebClient webClient,
      @Value("${API_KEY}") String apiKey,
      @Value("${GEOCODING_BASE_URL}") String baseUrl) {
    this.webClient = webClient;
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
  }

  /**
   * Call OpenWeather Geocoding API to get coordinates for a city.
   *
   * @param city City name
   * @param countryCode Country code
   * @param stateCode State code (optional)
   * @return Array of GeoLocation objects
   */
  public GeoLocation[] getGeoCoordinatesForCity(String city, String countryCode, String stateCode) {
    String combinedLocation =
        stateCode != null ? city + "," + countryCode + "," + stateCode : city + "," + countryCode;

    String url =
        UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("q", combinedLocation)
            .queryParam("limit", 1)
            .queryParam("appid", apiKey)
            .build()
            .toUriString();

    return webClient
        .get()
        .uri(url)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(GeoLocation[].class)
        .block();
  }
}
