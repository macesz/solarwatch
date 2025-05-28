package com.codecool.solarwatch.integrationTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codecool.solarwatch.SolarwatchApplication;
import com.codecool.solarwatch.controller.dto.MemberRegistrationDto;
import com.codecool.solarwatch.controller.dto.request.MemberRequest;
import com.codecool.solarwatch.model.entity.Member;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.UserRequest;
import com.codecool.solarwatch.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = SolarwatchApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = "classpath:application-test.properties")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class MemberControllerIT {
  @Autowired private MockMvc mockMvc;

  @Autowired private MemberRepository memberRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    memberRepository.deleteAll();
  }

  @DisplayName("Integration test - /api/user/register -> User registration")
  @Test
  void givenValidMemberRequest_whenRegister_thenReturnSuccess() throws Exception {
    MemberRequest memberRequest = new MemberRequest("testuser", "password123");

    mockMvc
        .perform(
            post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void givenValidUserRequest_whenSignIn_thenReturnJwtResponse() throws Exception {

    String username = "testuser";
    String password = "password123";
    MemberRegistrationDto memberRegistrationDto = new MemberRegistrationDto(username, password);

    mockMvc
        .perform(
            post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRegistrationDto)))
        .andExpect(status().isOk());
    // Verify user exists in database
    Optional<Member> savedMember = memberRepository.findByUsername(username);
    assertTrue(savedMember.isPresent(), "User should exist in database after registration");

    UserRequest userRequest = new UserRequest();
    userRequest.setUsername(username);
    userRequest.setPassword(password);

    String responseContent =
        mockMvc
            .perform(
                post("/api/user/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    System.out.println("Sign-in response: " + responseContent);

    // Parse the response and verify it
    JwtResponse jwtResponse = objectMapper.readValue(responseContent, JwtResponse.class);
    assertNotNull(jwtResponse.getToken(), "JWT token should not be null");
    assertEquals(username, jwtResponse.getUsername(), "Username in response should match");
    assertNotNull(jwtResponse.getRoles(), "Roles should not be null");
  }

  @Test
  void givenInvalidUserRequest_whenSignIn_thenReturnUnauthorized() throws Exception {
    MemberRequest memberRequest = new MemberRequest("nonexistent", "wrongpass");

    mockMvc
        .perform(
            post("/api/user/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
        .andExpect(status().isUnauthorized());
  }
}
