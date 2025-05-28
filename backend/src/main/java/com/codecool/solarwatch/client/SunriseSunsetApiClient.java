package com.codecool.solarwatch.client;

import com.codecool.solarwatch.controller.dto.SunriseSunsetResponseDTO;
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
   * Call external API to get the sunrise/sunset times according to geo-coordinates of a city.
   *
   * @param lat Latitude
   * @param lng Longitude
   * @param date The date the requested sunrise/sunset times.
   * @param tzid Time Zone ID.
   * @param formatted 0: ZonedDateTime format, 1: YYYY-MM-DD-hhss format.
   * @return SunriseSunsetResponseDTO, results are extracted with SunriseSunsetDTO.
   */
  public SunriseSunsetResponseDTO getSunriseSunsetByCoordinates(
      double lat, double lng, String date, String tzid, int formatted) {
    String url =
        UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("lat", lat)
            .queryParam("lng", lng)
            .queryParam("date", date)
            .queryParam("tzid", tzid)
            .queryParam("formatted", formatted)
            .build()
            .toUriString();

    try {
      return webClient
          .get()
          .uri(url)
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .bodyToMono(SunriseSunsetResponseDTO.class)
          .block();

    } catch (WebClientResponseException ex) {
      throw new ResponseStatusException(ex.getStatusCode(), ex.getResponseBodyAsString());
    }
  }
}
