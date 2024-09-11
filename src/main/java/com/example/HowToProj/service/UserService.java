package com.example.HowToProj.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.HowToProj.config.CustomPasswordEncoder;
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
public class UserService {
    private final UserRepository userRepository;
    private final CustomPasswordEncoder customPasswordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserService(UserRepository userRepository,
                        CustomPasswordEncoder customPasswordEncoder,
                        TokenProvider tokenProvider,
                        TokenService tokenService,
                        AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userRepository = userRepository;
        this.customPasswordEncoder = customPasswordEncoder;
        this.tokenProvider = tokenProvider;
        this.tokenService = tokenService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    // 회원가입
    @Transactional
    public UserDto signup(UserDto userDto) {
        // 이메일 중복 확인
        if (userRepository.findOneWithAuthoritiesByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 권한 부여
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 사용자 정의 Salt 생성 (예: 이메일의 일부를 사용하는 방식)
        String salt = userDto.getEmail().substring(0, 5);

        // 비밀번호를 사용자 정의 Salt와 함께 암호화
        String encryptedPassword = customPasswordEncoder.encode(userDto.getPassword(), salt);

        // User 객체 생성, 비밀번호는 CustomPasswordEncoder를 사용해 암호화
        User user = User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .nickname(userDto.getNickname())
                .password(encryptedPassword)  // 암호화된 비밀번호 저장
                .authorities(Collections.singleton(authority))
                .activated(true)
                .phoneNumber(userDto.getPhoneNumber())
                .birthDate(userDto.getBirthDate())
                .gender(userDto.getGender())
                .build();

        userRepository.save(user);

        // UserDto 객체 생성 및 반환
        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    // 로그인 기능
    @Transactional
    public TokenDto login(String email, String password) {
        // 이메일 존재 여부 확인
        Optional<User> userOpt = userRepository.findOneWithAuthoritiesByEmail(email);
        if (userOpt.isEmpty()) {
            throw new NotFoundMemberException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        User user = userOpt.get();

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        // 인증 처리
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 액세스 토큰 및 리프레시 토큰 생성
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(email);

        // 리프레시 토큰의 만료일을 1주일로 설정
        LocalDateTime newExpiryDate = LocalDateTime.now().plusWeeks(1);

        // 토큰 정보 저장
        tokenService.saveToken(newAccessToken, newRefreshToken, email, newExpiryDate);

        // 갱신된 토큰 정보를 TokenDto로 반환
        return TokenDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiryDate(newExpiryDate)
                .createdAt(LocalDateTime.now())  // 토큰 생성 시 현재 시간으로 설정
                .updatedAt(LocalDateTime.now())  // 토큰 생성 시 현재 시간으로 설정
                .build();
    }

    @Transactional
    public TokenDto autoLogin(String refreshToken) {
        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 유효하지 않음");
        }

        // 이메일 추출
        String email = tokenProvider.getUseremailFromToken(refreshToken);

        // 기존 리프레시 토큰의 만효기간 확인
        Date refreshTokenExpiry = tokenProvider.getExpirationDateFromToken(refreshToken);
        if (refreshTokenExpiry.before(new Date())) {
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

    // 회원탈퇴
    @Transactional
    public void deleteUser(String email, String password) {
        User user = userRepository.findOneWithAuthoritiesByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        // 사용자 정의 Salt 생성
        String salt = email.substring(0, 5);  // 예시로 이메일의 첫 5자를 Salt로 사용

        // 비밀번호 확인 (Salt 포함)
        if (!customPasswordEncoder.matches(password, salt, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자의 토큰 삭제
        tokenService.deleteTokenByEmail(email);

        // 사용자 삭제
        userRepository.delete(user);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findOneWithAuthoritiesByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        // 사용자 정의 Salt 생성
        String salt = email.substring(0, 5);  // 예시로 이메일의 첫 5자를 Salt로 사용

        // 기존 비밀번호 확인 (Salt 포함)
        if (!customPasswordEncoder.matches(oldPassword, salt, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호 설정 (비밀번호는 암호화하여 저장, Salt 포함)
        user.setPassword(customPasswordEncoder.encode(newPassword, salt));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        User user = userRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));

        // 사용자의 모든 토큰을 가져옴
        Set<TokenDto> tokens = user.getTokens().stream()
                .map(TokenDto::fromEntity)
                .collect(Collectors.toSet());

        return UserDto.fromEntity(user).toBuilder().tokens(tokens).build();
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new NotFoundMemberException("현재 로그인한 사용자를 찾을 수 없습니다."));

        User user = userRepository.findOneWithAuthoritiesByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundMemberException("현재 로그인한 사용자를 찾을 수 없습니다."));

        // 사용자의 모든 토큰을 가져옴
        Set<TokenDto> tokens = user.getTokens().stream()
                .map(TokenDto::fromEntity)
                .collect(Collectors.toSet());

        return UserDto.fromEntity(user).toBuilder().tokens(tokens).build();
    }
}