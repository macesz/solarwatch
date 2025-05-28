package com.codecool.solarwatch.model.entity;

import com.codecool.solarwatch.controller.dto.MemberDto;
import com.codecool.solarwatch.controller.dto.MemberRegistrationDto;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(name = "seq", initialValue = 2, allocationSize = 100)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
  private int id;

  @Column(unique = true)
  private String username;

  private String password;

  @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  // Constructors
  public Member() {}

  public Member(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public Member(MemberRegistrationDto memberRegistrationDto) {
    this.username = memberRegistrationDto.username();
    this.password = memberRegistrationDto.password();
  }

  public Member(MemberDto memberDto) {
    this.id = memberDto.id();
    this.username = memberDto.username();
  }

  @Override
  public String toString() {
    return "Member{" + "id=" + id + ", username='" + username + '\'' + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Member member = (Member) o;
    return id == member.id && Objects.equals(username, member.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username);
  }
}
