package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.controller.dto.MemberRegistrationDto;
import com.codecool.solarwatch.model.entity.Member;
import com.codecool.solarwatch.model.payload.JwtResponse;
import com.codecool.solarwatch.model.payload.UserRequest;
import com.codecool.solarwatch.security.jwt.JwtUtils;
import com.codecool.solarwatch.service.MemberService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class MemberController {

  private final MemberService memberService;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  @Autowired
  public MemberController(
      MemberService memberService,
      PasswordEncoder encoder,
      AuthenticationManager authenticationManager,
      JwtUtils jwtUtils) {
    this.memberService = memberService;
    this.encoder = encoder;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/register")
  public ResponseEntity<Boolean> createUser(@RequestBody MemberRegistrationDto signUpRequest) {
    return memberService.register(signUpRequest, encoder);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@RequestBody UserRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    User userDetails = (User) authentication.getPrincipal();
    List<String> roles =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    Optional<Member> loggedMember = memberService.findMemberByUserName(loginRequest.getUsername());

    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
  }

  @GetMapping("/me")
  public String me() {
    Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return "Hello " + member.getUsername();
  }

  @DeleteMapping("/admin/users/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable int id) {
    memberService.deleteMember(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/public")
  public String publicEndpoint() {
    return "Public\n";
  }
}
