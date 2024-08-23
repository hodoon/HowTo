package com.example.HowToProj.service;

import com.example.HowToProj.entity.Token;
import com.example.HowToProj.exception.TokenNotFoundException;
import com.example.HowToProj.jwt.TokenProvider;
import com.example.HowToProj.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    public TokenService(TokenRepository tokenRepository, TokenProvider tokenProvider) {
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
    }

    // 1. 토큰 저장
    public void saveToken(String accessToken,
                                String refreshToken,
                                String email,
                                LocalDateTime expiryDate){
        // 현재 시간을 가져와 createdAt, updatedA에 사용
        LocalDateTime now = LocalDateTime.now();

        // Token 엔티티를 생성
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(email)
                .expiryDate(expiryDate)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Token 엔티티를 데이터베이스에 저장
        tokenRepository.save(token);
    }

    @Transactional(readOnly = true)
    public Token findTokenByEmail(String email){
        return tokenRepository.findByUserEmail(email)
                .orElseThrow(() -> new TokenNotFoundException("Token not found for email" + email));
    }

    @Transactional
    public Token updateToken(String email, String newAccessToken, String newRefreshToken, LocalDateTime newExpiryDate){
        //기존 토큰 검색
        Token token = findTokenByEmail(email);

        // 토큰 정보 갱신
        token.setAccessToken(newAccessToken);
        token.setRefreshToken(newRefreshToken);
        token.setExpiryDate(newExpiryDate);
        token.setUpdatedAt(LocalDateTime.now());

        // 갱신된 Token 엔티티를 데이터베이스에 저장
        return tokenRepository.save(token);
    }

    @Transactional
    public void deleteTokenByEmail(String email){
        if(!tokenRepository.existsByUserEmail(email)){
            throw new TokenNotFoundException("Token not found for email" + email);
        }

        tokenRepository.deleteByUserEmail(email);
    }

    @Transactional
    public boolean validateRefreshToken(String refreshToken){
        if(!tokenProvider.validateToken(refreshToken)){
            return false;
        }

        return tokenRepository.findByRefreshToken(refreshToken).isPresent();
    }

    public boolean isTokenExpired(String token){
        try {
            // Claims를 얻어오기 위해 토큰을 파싱합니다.
            Claims claims = tokenProvider.getClaims(token);
            // 현재 시간과 비교하여 토큰의 만료 시간을 확인합니다.
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e){
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void invalidateToken(String accessToken){
        Token token = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("NotFoundToken"));

        tokenRepository.delete(token);
    }


}
