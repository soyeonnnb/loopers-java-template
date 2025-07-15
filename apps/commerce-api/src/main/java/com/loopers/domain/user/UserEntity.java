package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

    private String loginId;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private String birthDate;

    private String gender;

    protected UserEntity() {}

    static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,10}$");
    static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    static final Pattern BIRTH_PATTERN = Pattern.compile("^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

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
        if (gender == null || gender.isBlank()) {
            throw new CoreException(UserErrorType.GENDER_CANNOT_BE_NULL);
        }

        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
    }
    public String getEmail() { return this.email; }
    public String getName() {
        return this.name;
    }

    public String getLoginId() { return this.loginId; }
    public String getPassword() { return this.password; }
    public String getNickname() { return this.nickname; }
    public String getBirthDate() { return this.birthDate; }

    public String getGender() { return this.gender; }
}
