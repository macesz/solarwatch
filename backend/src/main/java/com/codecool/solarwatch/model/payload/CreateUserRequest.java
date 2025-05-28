package com.codecool.solarwatch.model.payload;

import lombok.Data;
import lombok.Getter;

@Data
public class CreateUserRequest {
  private String username;
  private String password;
  @Getter private boolean admin;
}
