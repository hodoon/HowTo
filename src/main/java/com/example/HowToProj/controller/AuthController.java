package com.example.HowToProj.controller;

import com.example.HowToProj.dto.LoginDto;
import com.example.HowToProj.dto.TokenDto;
import com.example.HowToProj.entity.Token;
import com.example.HowToProj.jwt.JwtFilter;
import com.example.HowToProj.jwt.TokenProvider;

import com.example.HowToProj.service.TokenService;
import com.example.HowToProj.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, TokenService tokenService, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {
        TokenDto tokenDto = userService.login(loginDto.getEmail(), loginDto.getPassword());

        if (tokenDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());

        return new ResponseEntity<>(tokenDto, httpHeaders, HttpStatus.OK);
    }

    // 자동 로그인 엔드포인트
    @PostMapping("/auto-login")
    public ResponseEntity<TokenDto> autoLogin(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        TokenDto tokenDto = userService.autoLogin(refreshToken);

        if (tokenDto == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return ResponseEntity.ok(tokenDto);
    }

    // 로그아웃 엔드 포인트
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 리프레쉬 토큰에서 사용자 이메일 추출
        String email = tokenProvider.getUseremailFromToken(refreshToken);

        // 로그아웃 처리
        userService.logout(email);

        // 로그아웃 성공 시 OK 상태 반환
        return ResponseEntity.ok().build();
    }

    // 자동 로그아웃 엔드포인트
    @PostMapping("/auto-logout")
    public ResponseEntity<Void> autoLogout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        try {
            // 토큰 만료 처리 및 자동 로그아웃 수행
            userService.handleTokenExpiration(refreshToken);
        } catch (Exception e) {
            // 토큰이 유효하지 않으면 로그아웃 처리 실패로 간주하고 401 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 로그아웃 성공 시 OK 상태 반환
        return ResponseEntity.ok().build();
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken) || !tokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 리프레쉬 토큰에서 사용자 이메일 추출
        String userEmail = tokenProvider.getUseremailFromToken(refreshToken);

        // 새로운 엑세스 토큰 생성
        String newAccessToken = tokenProvider.createAccessToken(tokenProvider.getAuthentication(refreshToken));
        LocalDateTime newExpiryDate = tokenProvider.getExpirationDateFromToken(newAccessToken).toInstant()
                .atZone(tokenProvider.getZoneId())
                .toLocalDateTime();

        // 토큰 업데이트
        Token updatedToken = tokenService.updateToken(userEmail, newAccessToken, refreshToken, newExpiryDate);

        // TokenDto 생성 및 반환
        return ResponseEntity.ok(TokenDto.fromEntity(updatedToken));

    }

}

