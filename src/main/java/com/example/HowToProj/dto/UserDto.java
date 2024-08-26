package com.example.HowToProj.dto;

import com.example.HowToProj.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일 길이는 100자를 초과할 수 없습니다.")
    private String email;

    @NotNull(message = "사용자 이름은 필수입니다.")
    @Size(min = 3, max = 50, message = "사용자 이름 길이는 3자 이상 50자 이하이어야 합니다.")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "비밀번호는 필수입니다.")
    @Size(min = 3, max = 100, message = "비밀번호 길이는 3자 이상 100자 이하이어야 합니다.")
    private String password;

    @NotNull(message = "닉네임은 필수입니다.")
    @Size(min = 3, max = 50, message = "닉네임 길이는 3자 이상 50자 이하이어야 합니다.")
    private String nickname;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    private Set<AuthorityDto> authorityDtoSet;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Set<TokenDto> tokens;

    public static UserDto fromEntity(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .tokens(user.getTokens().stream()
                        .map(TokenDto::fromEntity)
                        .collect(Collectors.toSet()))
                .build();
    }

    // toBuilder 메서드 추가
    public UserDto.UserDtoBuilder toBuilder() {
        return UserDto.builder()
                .email(this.email)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .authorityDtoSet(this.authorityDtoSet)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .deletedAt(this.deletedAt)
                .tokens(this.tokens);
    }
}
