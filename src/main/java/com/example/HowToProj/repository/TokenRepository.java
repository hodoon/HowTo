package com.example.HowToProj.repository;

import com.example.HowToProj.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    // AccessToken으로 토큰 검색
    Optional<Token> findByAccessToken(String accessToken);

    // RefreshToken으로 토큰 검색
    Optional<Token> findByRefreshToken(String refreshToken);

    // 유저 이름으로 토큰 검색
    Optional<Token> findByUsername(String username);

    // 유저 Email로 토큰 검색
    Optional<Token> findByUserEmail(String email);

    // AccessToken을 사용하여 토큰 삭제
    void deleteByAccessToken(String accessToken);

    // RefreshToken을 사용하여 토큰 삭제
    void deleteByRefreshToken(String refreshToken);

    // 이메일로 모든 토큰 삭제
    void deleteByUserEmail(String email);

    boolean existsByUserEmail(String Email);
}
