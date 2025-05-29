package com.codecool.solarwatch.client;

import com.codecool.solarwatch.model.SolarWatchReport;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SunriseSunsetApiClient {

  private final WebClient webClient;
  private final String baseUrl;

  public SunriseSunsetApiClient(
      WebClient webClient, @Value("${SUNRISESUNSET_BASE_URL}") String baseUrl) {
    this.webClient = webClient;
    this.baseUrl = baseUrl;
  }

  /**
   * Call Sunrise-Sunset API to get sunrise/sunset times for coordinates.
   *
   * @param latitude Latitude
   * @param longitude Longitude
   * @param date Date for sunrise/sunset data
   * @return SolarWatchReport containing sunrise/sunset times
   */
  public SolarWatchReport getSunriseSunsetByCoordinates(
      double latitude, double longitude, LocalDate date) {
    String url =
        UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("lat", String.format("%.1f", latitude))
            .queryParam("lng", String.format("%.1f", longitude))
            .queryParam("date", date.toString())
            .build()
            .toUriString();

    try {
      return webClient
          .get()
          .uri(url)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(SolarWatchReport.class)
          .block();
    } catch (WebClientResponseException ex) {
      throw new ResponseStatusException(
          ex.getStatusCode(),
          "Failed to fetch sunrise/sunset data: " + ex.getResponseBodyAsString());
    }
  }
}
