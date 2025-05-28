package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.entity.Member;
import com.codecool.solarwatch.repository.MemberRepository;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member =
        memberRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    // Convert roles to authorities with ROLE_ prefix
    Collection<GrantedAuthority> authorities =
        member.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())) // Add ROLE_ prefix!
            .collect(Collectors.toList());

    return new User(member.getUsername(), member.getPassword(), authorities);
  }
}
