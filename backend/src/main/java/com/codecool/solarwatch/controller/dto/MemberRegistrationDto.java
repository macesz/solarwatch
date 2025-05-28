package com.codecool.solarwatch.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MemberRegistrationDto(@JsonAlias("username") String username, String password) {}
