package com.codecool.solarwatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SunsetSunrise {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "city_id") // it would be the same without this line
  private City city;

  private LocalDate date;
  private String sunrise;
  private String sunset;
  private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSunrise() {
    return sunrise;
  }

  public void setSunrise(String sunrise) {
    this.sunrise = sunrise;
  }

  public String getSunset() {
    return sunset;
  }

  public void setSunset(String sunset) {
    this.sunset = sunset;
  }

  public void setCity(City city) {
    this.city = city;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalDate getDate() {
    return date;
  }

  public City getCity() {
    return city;
  }

  public Long getId() {
    return id;
  }
}
