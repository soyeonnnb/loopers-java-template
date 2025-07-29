package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.PointErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Schema(name = "로그인 ID")
    @Column(nullable = false, length = 10, unique = true)
    private String loginId;

    @Schema(name = "비밀번호")
    @Column(nullable = false)
    private String password;

    @Schema(name = "이메일")
    @Column(nullable = false)
    private String email;

    @Schema(name = "이름")
    @Column(nullable = false)
    private String name;

    @Schema(name = "닉네임")
    @Column(nullable = false)
    private String nickname;

    @Schema(name = "생년월일")
    @Column(nullable = false)
    private LocalDate birthDate;

    @Schema(name = "성별")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Schema(name = "포인트")
    @Column(nullable = false)
    private Long point;

    protected UserEntity() {
    }

    public UserEntity(String loginId, String password, String email, String name, String nickname, String birthDate, String gender) {

        UserValidator.validateLoginId(loginId);
        UserValidator.validateEmail(email);

        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = stringToLocalDate(birthDate);
        this.gender = Gender.from(gender);
        this.point = 0L;
    }

    private LocalDate stringToLocalDate(String birthDate) {
        UserValidator.validateBirthDate(birthDate);

        String[] inputs = birthDate.split("-");
        return LocalDate.of(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]));
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getLoginId() {
        return this.loginId;
    }

    public String getPassword() {
        return this.password;
    }

    public String getNickname() {
        return this.nickname;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Long getPoint() {
        return this.point;
    }

    public void chargePoint(Long point) {
        if (point <= 0) {
            throw new CoreException(PointErrorType.POINT_MUST_BE_GREATER_THAN_0);
        }
        this.point += point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(loginId, that.loginId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginId);
    }
}
