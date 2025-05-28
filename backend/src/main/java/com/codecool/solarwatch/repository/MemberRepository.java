package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
  public void deleteMemberById(int id);

  public Optional<Member> getMemberById(int id);

  public Optional<Member> findByUsername(String username);
}
