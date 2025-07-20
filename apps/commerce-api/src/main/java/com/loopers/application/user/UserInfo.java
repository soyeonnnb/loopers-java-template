package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.UserEntity;

import java.time.LocalDate;


public record UserInfo(Long id, String loginId, String email, LocalDate birthDate, Gender gender) {
    public static UserInfo from(UserEntity userEntity) {
        return new UserInfo(userEntity.getId(), userEntity.getLoginId(), userEntity.getEmail(), userEntity.getBirthDate(), userEntity.getGender());
    }
}
