package com.loopers.interfaces.api.user;

import com.loopers.application.example.ExampleInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UserV1Dto {
    public record UserResponse(Long id, String loginId, String email, String birthDate) {
        public static UserResponse from(UserInfo user) {
            return new UserResponse(
                user.id(), user.loginId(), user.email(), user.birthDate()
            );
        }
    }

    public record UserRegisterRequest(
            @NotBlank String loginId,
            @NotBlank
            String email,
            @NotBlank
            String password,
            String gender,
            @NotBlank
            String birthDate,
            String nickname
    ) {
        public UserEntity to() {
            return new UserEntity(loginId, password, email, nickname, nickname, birthDate, gender);
        }

    }
}
