package com.codecool.solarwatch.model.payload;

import java.util.List;

public record JwtResponse(String jwtToken, String userName, List<String> roles) {
  public Object getToken() {
    return jwtToken;
  }

  public String getUsername() {
    return userName;
  }

  public Object getRoles() {
    return roles;
  }
}
