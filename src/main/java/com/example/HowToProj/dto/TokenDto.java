package com.example.HowToProj.dto;

import com.example.HowToProj.entity.Token;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TokenDto {

    private final String accessToken;
    private final String refreshToken;
    private final LocalDateTime expiryDate;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static TokenDto fromEntity(Token token) {
        return TokenDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiryDate(token.getExpiryDate())
                .username(token.getUsername())
                .email(token.getEmail())
                .createdAt(token.getCreatedAt())
                .updatedAt(token.getUpdatedAt())
                .build();
    }
}
