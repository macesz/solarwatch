package com.codecool.solarwatch.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SunriseSunsetResponseDTO(SunriseSunsetDTO results) {}
