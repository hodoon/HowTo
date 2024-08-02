package com.example.HowToProj.repository;

import com.example.HowToProj.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(Long id);
    boolean existsByNickname(String nickname);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
