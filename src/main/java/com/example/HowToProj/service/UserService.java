package com.example.HowToProj.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import com.example.HowToProj.dto.TokenDto;
import com.example.HowToProj.dto.UserDto;
import com.example.HowToProj.entity.*;
import com.example.HowToProj.exception.DuplicateMemberException;
import com.example.HowToProj.exception.NotFoundMemberException;
import com.example.HowToProj.jwt.TokenProvider;
import com.example.HowToProj.repository.UserRepository;
import com.example.HowToProj.util.SecurityUtil;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenProvider tokenProvider,
                        TokenService tokenService,
                        AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.tokenService = tokenService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }
    // 회원가입
    @Transactional
    public UserDto signup(UserDto userDto) {
        //이메일중복 확인
        if (userRepository.findOneWithAuthoritiesByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }
        // 권한부여
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // User객체 생성, 비밀번호는 PasswordEncorder를 사용후 암호화
        User user = User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .nickname(userDto.getNickname())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        userRepository.save(user);

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //엑세스 토큰 및 리프레시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getEmail());

        //엑세스 토큰 만료 시간 계산
        LocalDateTime expiryDate = tokenProvider.getExpirationDateFromAccessToken(accessToken)
                .toInstant()
                .atZone(tokenProvider.getZoneId())
                .toLocalDateTime();

        //토큰 저장
        tokenService.saveToken(accessToken, refreshToken, userDto.getEmail(), expiryDate);

        return UserDto.builder()
                .username(userDto.getUsername())
                .token(TokenDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiryDate(expiryDate)
                        .build())
                .build();
    }

    // 로그인 기능
    @Transactional
    public TokenDto login(String email, String password){
        // 1. 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        // 2. 인증 처리
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 액세스 토큰 및 리프레시 토큰 생성
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(email);

        // 4. 리프레시 토큰의 만료일을 1주일로 설정
        LocalDateTime newExpiryDate = LocalDateTime.now().plusWeeks(1);

        // 5. 토큰 정보 갱신
        Token updatedToken = tokenService.updateToken(email, newAccessToken, newRefreshToken, newExpiryDate);

        // 6. 갱신된 토큰 정보를 TokenDto로 반환
        return TokenDto.fromEntity(updatedToken);
    }

    @Transactional
    public TokenDto autoLogin(String refreshToken){
        // 리프레시 토큰 검증
        if(!tokenProvider.validateToken(refreshToken)){
            throw new RuntimeException("리프레시 토큰이 유효하지 않음");
        }

        // 이메일 추출
        String email = tokenProvider.getUseremailFromToken(refreshToken);

        // 기존 리프레시 토큰의 만효기간 확인
        Date refreshTokenExpiry =  tokenProvider.getExpirationDateFromToken(refreshToken);
        if (refreshTokenExpiry.before(new Date())){
            throw new RuntimeException("리프레시 토큰이 만료됨");
        }

        // 새로운 엑세스 토큰 및 리프레시 토큰 발급
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        // 리프레시 토큰의 유효기간을 1주일로 초기화
        String newRefreshToken = tokenProvider.createRefreshToken(email);

        // 새로운 리프레시 토큰의 만료 기간
        LocalDateTime newExpiryDate = LocalDateTime.now().plusWeeks(1);

        // 기존 토큰 갱신
        Token updatedToken = tokenService.updateToken(email, newAccessToken, newRefreshToken, newExpiryDate);

        // 갱신된 토큰 DTO 반환
        return TokenDto.fromEntity(updatedToken);
    }

    // 로그아웃 메소드
    @Transactional
    public void logout(String email) {
        // 사용자의 토큰 삭제
        tokenService.deleteTokenByEmail(email);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    // 리프레시 토큰 만료시 자동 로그아웃
    @Transactional
    public void handleTokenExpiration(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            String email = tokenProvider.getUseremailFromToken(refreshToken);
            logout(email);  // 자동 로그아웃 처리
        }
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
