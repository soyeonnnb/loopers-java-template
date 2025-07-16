package com.loopers.interfaces.api.user;

import com.loopers.application.example.ExampleInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UserV1Dto {
    public record UserResponse(Long id, String loginId, String email, String birthDate, String gender) {
        public static UserResponse from(UserInfo user) {
            return new UserResponse(
                user.id(), user.loginId(), user.email(), user.birthDate(), user.gender()
            );
        }
    }

    public record UserRegisterRequest(
            @NotBlank
            @Schema(example = "la28s5d")
            String loginId,
            @NotBlank
            @Schema(example = "la28s5d@naver.com")
            String email,
            @NotBlank
            String password,

            @Schema(example = "F")
            String gender,
            @NotBlank
            @Schema(example = "2025-01-01")
            String birthDate,
            String nickname
    ) {
        public UserEntity to() {
            return new UserEntity(loginId, password, email, nickname, nickname, birthDate, gender);
        }

    }
}
