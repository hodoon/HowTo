package com.example.HowToProj.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotNull
    @Size(min = 3, max = 50)
    @Email // email 유효성 검사
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;

}
