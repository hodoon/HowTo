package com.example.HowToProj.controller;


import com.example.HowToProj.dto.LoginDto;
import com.example.HowToProj.dto.TokenDto;
import com.example.HowToProj.dto.UserDto;
import com.example.HowToProj.handler.ErrorResponse;
import com.example.HowToProj.jwt.TokenProvider;
import com.example.HowToProj.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 오류 메시지를 수집합니다.
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            // ErrorResponse 객체를 생성하여 반환합니다.
            ErrorResponse errorResponse = new ErrorResponse("유효성 검사 오류", errors);

            // 오류 로그를 기록합니다.
            log.warn("Signup validation failed: {}", errors);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        log.info("Processing signup request for user: {}", userDto.getUsername());
        return ResponseEntity.ok(userService.signup(userDto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserDto> getMyUserInfo(HttpServletRequest request) {
        log.info("Fetching user info for request: {}", request.getRequestURI());
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
        log.info("Fetching user info for username: {}", username);
        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
    }
}
