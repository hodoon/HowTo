package com.example.HowToProj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CustomPasswordEncoder {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomPasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String encode(String rawPassword, String salt) {
        String saltedPassword = rawPassword + salt;
        return bCryptPasswordEncoder.encode(saltedPassword);
    }

    public boolean matches(String rawPassword, String salt, String encodedPassword) {
        String saltedPassword = rawPassword + salt;
        return bCryptPasswordEncoder.matches(saltedPassword, encodedPassword);
    }
}
