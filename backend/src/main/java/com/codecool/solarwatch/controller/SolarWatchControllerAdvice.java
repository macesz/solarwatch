package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.customErrorHandling.InvalidLocationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SolarWatchControllerAdvice {
  @ResponseBody
  @ExceptionHandler(InvalidLocationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String invalidDateExceptionHandler(InvalidLocationException ex) {
    return ex.getMessage();
  }
}
