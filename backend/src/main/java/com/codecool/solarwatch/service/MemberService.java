package com.codecool.solarwatch.service;

import com.codecool.solarwatch.controller.dto.MemberDto;
import com.codecool.solarwatch.controller.dto.MemberRegistrationDto;
import com.codecool.solarwatch.model.entity.Member;
import com.codecool.solarwatch.model.entity.Role;
import com.codecool.solarwatch.repository.MemberRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberService(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public ResponseEntity<Boolean> register(
      MemberRegistrationDto signUpRequest, PasswordEncoder encoder) {
    Member member = new Member();
    member.setUsername(signUpRequest.username());
    member.setPassword(encoder.encode(signUpRequest.password()));
    member.setRoles(Set.of(Role.USER));
    memberRepository.save(member);
    return ResponseEntity.ok(true);
  }

  public MemberDto getMember(int id) {
    Member member = memberRepository.getMemberById(id).orElseThrow(NoSuchElementException::new);
    return new MemberDto(member);
  }

  public void deleteMember(int id) {
    memberRepository.deleteMemberById(id);
  }

  public boolean updateUser(Member member) {
    memberRepository.save(member);
    return true;
  }

  public Optional<Member> findMemberByUserName(String username) {
    return memberRepository.findByUsername(username);
  }
}
