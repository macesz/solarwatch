package com.codecool.solarwatch.customErrorHandling;

public class InvalidLocationException extends RuntimeException {
  public InvalidLocationException() {
    super("Location not valid");
  }

  public InvalidLocationException(String message) {
    super(message);
  }
}
