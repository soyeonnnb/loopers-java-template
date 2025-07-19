package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern BIRTH_PATTERN = Pattern.compile("^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");
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

        if (loginId == null || loginId.isBlank() || !LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new CoreException(UserErrorType.INVALID_LOGIN_ID);
        }

        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CoreException(UserErrorType.INVALID_EMAIL);
        }

        if (birthDate == null || birthDate.isBlank() || !BIRTH_PATTERN.matcher(birthDate).matches()) {
            throw new CoreException(UserErrorType.INVALID_BIRTH_DATE);
        }

        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = stringToLocalDate(birthDate);
        this.gender = Gender.from(gender);
        this.point = 0L;
    }

    private LocalDate stringToLocalDate(String input) {
        String[] inputs = input.split("-");
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
        this.point += point;
    }
}
