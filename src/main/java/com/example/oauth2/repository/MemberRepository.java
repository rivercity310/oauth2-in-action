package com.example.oauth2.repository;

import com.example.oauth2.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findMemberByUsername(String username);
}
