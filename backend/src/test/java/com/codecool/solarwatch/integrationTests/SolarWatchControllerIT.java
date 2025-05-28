package com.codecool.solarwatch.integrationTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codecool.solarwatch.customErrorHandling.InvalidLocationException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SolarWatchReport;
import com.codecool.solarwatch.model.SolarWatchResult;
import com.codecool.solarwatch.model.entity.Member;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.repository.MemberRepository;
import com.codecool.solarwatch.service.GeoLocationService;
import com.codecool.solarwatch.service.SolarWatchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class SolarWatchControllerIT {

  @Autowired private MockMvc mockMvc;

  @Autowired private MemberRepository memberRepository;

  @MockBean private GeoLocationService geoLocationService;

  @MockBean private SolarWatchService solarWatchService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private PasswordEncoder passwordEncoder;

  private Member testMember;
  private String jwtToken;

  @BeforeEach
  public void setup() throws Exception {
    memberRepository.deleteAll();

    // Create test user with correct setter
    testMember = new Member();
    testMember.setUsername("testUser"); // Fixed: using setUsername() instead of setName()
    testMember.setPassword(passwordEncoder.encode("password"));
    testMember.setRoles(Set.of(Role.USER, Role.ADMIN)); // Added both roles to ensure access
    memberRepository.save(testMember);

    // Login to get JWT token
    String loginCredentials =
        """
                {
                "username": "testUser",
                "password": "password"
                }
                """;

    String responseContent =
        mockMvc
            .perform(
                post("/api/user/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginCredentials))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Extract JWT token directly from JSON response
    JsonNode jsonNode = objectMapper.readTree(responseContent);
    jwtToken = jsonNode.get("jwtToken").asText();

    System.out.println("Successfully extracted JWT token: " + (jwtToken != null ? "✓" : "✗"));
  }

  @AfterEach
  public void tearDown() {
    memberRepository.deleteAll();
  }

  @Test
  void getData_withValidParametersWithoutState_shouldReturnSolarWatchReport() throws Exception {
    // Arrange
    String testCity = "London";
    String testCountryCode = "GB";
    String testDate = "2024-04-22";
    LocalDate date = LocalDate.parse(testDate);

    City mockCity = new City();
    mockCity.setId(1L);
    mockCity.setName(testCity);
    mockCity.setCountry(testCountryCode);
    mockCity.setLatitude(51.5074);
    mockCity.setLongitude(-0.1278);

    SolarWatchResult mockResult = new SolarWatchResult("06:30:00 AM", "07:45:00 PM");
    SolarWatchReport mockReport = new SolarWatchReport("OK", mockResult);

    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), eq(null)))
        .thenReturn(mockCity);

    when(solarWatchService.getReport(eq(mockCity), eq(date))).thenReturn(mockReport);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.results.sunrise").value("06:30:00 AM"))
        .andExpect(jsonPath("$.results.sunset").value("07:45:00 PM"));
  }

  @Test
  void getData_withValidParametersWithState_shouldReturnSolarWatchReport() throws Exception {
    // Arrange
    String testCity = "Austin";
    String testCountryCode = "US";
    String testStateCode = "TX";
    String testDate = "2024-05-15";
    LocalDate date = LocalDate.parse(testDate);

    City mockCity = new City();
    mockCity.setId(2L);
    mockCity.setName(testCity);
    mockCity.setCountry(testCountryCode);
    mockCity.setState(testStateCode);
    mockCity.setLatitude(30.2672);
    mockCity.setLongitude(-97.7431);

    SolarWatchResult mockResult = new SolarWatchResult("06:45:00 AM", "08:30:00 PM");
    SolarWatchReport mockReport = new SolarWatchReport("OK", mockResult);

    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), eq(testStateCode)))
        .thenReturn(mockCity);

    when(solarWatchService.getReport(eq(mockCity), eq(date))).thenReturn(mockReport);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("stateCode", testStateCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.results.sunrise").value("06:45:00 AM"))
        .andExpect(jsonPath("$.results.sunset").value("08:30:00 PM"));
  }

  @Test
  void getData_withInvalidLocation_shouldReturnBadRequest() throws Exception {
    // Arrange
    String testCity = "InvalidCity";
    String testCountryCode = "XX";
    String testDate = "2024-04-22";
    String errorMessage = "Location not valid";

    // Make sure InvalidLocationException extends RuntimeException and has proper constructor
    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), any()))
        .thenThrow(new InvalidLocationException("Location not valid"));

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(errorMessage));
  }

  @Test
  void getData_withMissingCityParam_shouldReturnBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("countryCode", "GB")
                .param("date", "2024-04-22")
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getData_withMissingCountryCodeParam_shouldReturnBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "London")
                .param("date", "2024-04-22")
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getData_withMissingDateParam_shouldReturnBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "London")
                .param("countryCode", "GB")
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getData_withInvalidDateFormat_shouldReturnBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "London")
                .param("countryCode", "GB")
                .param("date", "22-04-2024") // Invalid format
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getData_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "London")
                .param("countryCode", "GB")
                .param("date", "2024-04-22"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getData_withInvalidToken_shouldReturnUnauthorized() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "London")
                .param("countryCode", "GB")
                .param("date", "2024-04-22")
                .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getData_serviceThrowsRuntimeException_shouldReturnInternalServerError() throws Exception {
    // Arrange
    String testCity = "London";
    String testCountryCode = "GB";
    String testDate = "2024-04-22";
    LocalDate date = LocalDate.parse(testDate);

    City mockCity = new City();
    mockCity.setId(1L);
    mockCity.setName(testCity);
    mockCity.setCountry(testCountryCode);

    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), any()))
        .thenReturn(mockCity);

    when(solarWatchService.getReport(eq(mockCity), eq(date)))
        .thenThrow(new RuntimeException("Service unavailable"));

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isInternalServerError()); // Should now work
  }

  @Test
  void getData_withSpecialCharactersInCityName_shouldWork() throws Exception {
    // Arrange
    String testCity = "São Paulo";
    String testCountryCode = "BR";
    String testDate = "2024-04-22";
    LocalDate date = LocalDate.parse(testDate);

    City mockCity = new City();
    mockCity.setId(3L);
    mockCity.setName(testCity);
    mockCity.setCountry(testCountryCode);

    SolarWatchResult mockResult = new SolarWatchResult("06:00:00 AM", "06:00:00 PM");
    SolarWatchReport mockReport = new SolarWatchReport("OK", mockResult);

    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), any()))
        .thenReturn(mockCity);

    when(solarWatchService.getReport(eq(mockCity), eq(date))).thenReturn(mockReport);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OK"));
  }

  @Test
  void getData_withEmptyStringParams_shouldReturnBadRequest() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", "") // Empty string
                .param("countryCode", "GB")
                .param("date", "2024-04-22")
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isBadRequest()); // Should now work
  }

  @Test
  void getData_withFutureDate_shouldWork() throws Exception {
    // Arrange
    String testCity = "Tokyo";
    String testCountryCode = "JP";
    String testDate = "2025-06-15";
    LocalDate date = LocalDate.parse(testDate);

    City mockCity = new City();
    mockCity.setId(4L);
    mockCity.setName(testCity);
    mockCity.setCountry(testCountryCode);

    SolarWatchResult mockResult = new SolarWatchResult("04:30:00 AM", "07:00:00 PM");
    SolarWatchReport mockReport = new SolarWatchReport("OK", mockResult);

    when(geoLocationService.getCityInfo(eq(testCity), eq(testCountryCode), any()))
        .thenReturn(mockCity);

    when(solarWatchService.getReport(eq(mockCity), eq(date))).thenReturn(mockReport);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/sunset-sunrise")
                .param("city", testCity)
                .param("countryCode", testCountryCode)
                .param("date", testDate)
                .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OK")); // Fixed typo: andExpect not andExpected
  }

  @Test
  void debugLogin_shouldShowLoginDetails() throws Exception {
    // Debug test to verify login process
    String loginCredentials =
        """
                {
                "username": "testUser",
                "password": "password"
                }
                """;

    mockMvc
        .perform(
            post("/api/user/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginCredentials))
        .andDo(print()) // This will print detailed request/response info
        .andExpect(status().isOk());
  }
}
