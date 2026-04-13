package com.example.member.repository;

import com.example.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySchoolEmail(String schoolEmail);
    Optional<User> findByNickname(String nickname);
}