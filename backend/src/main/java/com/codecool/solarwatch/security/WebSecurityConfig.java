package com.codecool.solarwatch.security;

import com.codecool.solarwatch.security.jwt.AuthEntryPointJwt;
import com.codecool.solarwatch.security.jwt.AuthTokenFilter;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
  private final UserDetailsService userDetailsService;
  private final AuthEntryPointJwt unauthorizedHandler;
  private final JwtUtils jwtUtils;

  @Autowired
  public WebSecurityConfig(
      UserDetailsService userDetailsService,
      AuthEntryPointJwt unauthorizedHandler,
      JwtUtils jwtUtils) {
    this.userDetailsService = userDetailsService;
    this.unauthorizedHandler = unauthorizedHandler;
    this.jwtUtils = jwtUtils;
  }

  @Bean // Add @Bean annotation here!
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter(jwtUtils, userDetailsService);
  }

  @Bean // Add @Bean annotation here!
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/user/register", "/api/user/signin", "/api/user/public")
                    .permitAll()
                    .requestMatchers("/api/sunset-sunrise/**") // Explicitly allow this endpoint
                    .authenticated()
                    .requestMatchers("/api/user/me")
                    .hasRole("USER")
                    .requestMatchers("/api/user/admin/create-user/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/user/admin/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/user/admin/users/{username}/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(
        authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
