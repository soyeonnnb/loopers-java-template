package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class UserV1Dto {
    public record UserResponse(Long id, String loginId, String email, LocalDate birthDate, Gender gender) {
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
            @Schema(example = "FEMALE")
            String gender,
            @NotBlank
            @Schema(example = "2025-01-01")
            String birthDate,
            @NotBlank
            @Schema(example = "소연")
            String nickname,
            @NotBlank
            @Schema(example = "김소연")
            String name
    ) {
        public UserEntity to(String encodedPassword) {
            return new UserEntity(loginId, encodedPassword, email, name, nickname, birthDate, gender);
        }

    }
}
