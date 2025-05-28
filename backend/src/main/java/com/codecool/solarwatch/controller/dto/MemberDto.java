package com.codecool.solarwatch.controller.dto;

import com.codecool.solarwatch.model.entity.Member;

public record MemberDto(int id, String username) {
  public MemberDto(Member member) {
    this(member.getId(), member.getUsername());
  }
}
