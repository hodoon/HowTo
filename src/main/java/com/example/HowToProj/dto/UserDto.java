package com.example.HowToProj.dto;

import com.example.HowToProj.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @Email
    @Size(max = 100)
    private String email;

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;

    @NotNull
    @Size(max = 20)
    private String phoneNumber;

    @JsonProperty("token")
    private TokenDto token;

    private Set<AuthorityDto> authorityDtoSet;

    private LocalDateTime createdAt;  // 가입일시
    private LocalDateTime updatedAt;  // 수정일시
    private LocalDateTime deletedAt;  // 삭제일시

    public static UserDto from(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())  // 가입일시 추가
                .updatedAt(user.getUpdatedAt())  // 수정일시 추가
                .deletedAt(user.getDeletedAt())  // 삭제일시 추가
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
