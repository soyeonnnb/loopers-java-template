package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.PointErrorType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Column(name = "로그인 ID", nullable = false, length = 10, unique = true)
    private String loginId;

    @Column(name = "비밀번호", nullable = false)
    private String password;

    @Column(name = "이메일", nullable = false)
    private String email;

    @Column(name = "이름", nullable = false)
    private String name;

    @Column(name = "닉네임", nullable = false)
    private String nickname;

    @Column(name = "생년월일", nullable = false)
    private LocalDate birthDate;

    @Column(name = "성별", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "포인트", nullable = false)
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
}
