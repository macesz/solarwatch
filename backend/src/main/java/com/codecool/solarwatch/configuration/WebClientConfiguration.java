package com.codecool.solarwatch.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfiguration {
  @Bean
  public WebClient webClient() {
    // Customize HttpClient if needed (e.g., timeouts, connection pooling)
    HttpClient httpClient =
        HttpClient.create().responseTimeout(java.time.Duration.ofSeconds(10)); // Set timeout

    // Create a connector with the customized HttpClient
    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    // Build WebClient with the connector
    return WebClient.builder().clientConnector(connector).build();
  }
}
