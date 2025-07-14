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
public class UserModel extends BaseEntity {

    private String loginId;
    private String password;
    private String email;
    private String name;
    private String nickname;
    private LocalDate birthDate;

    protected UserModel() {}

    static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[A-Za-z]{2,6}$");
    static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    public UserModel(String loginId, String password, String email, String name, String nickname, LocalDate birthDate) {

        if (loginId == null || loginId.isBlank() || !LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new CoreException(UserErrorType.INVALID_LOGIN_ID, "로그인 ID 형식이 유효하지 않습니다.");
        }
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CoreException(UserErrorType.INVALID_EMAIL, "이메일 형식이 유효하지 않습니다.");
        }

        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
    }
    public String getEmail() { return this.email; }
    public String getName() {
        return this.name;
    }

    public String getLoginId() { return this.loginId; }
    public String getPassword() { return this.password; }
    public String getNickname() { return this.nickname; }
    public LocalDate getBirthDate() { return this.birthDate; }
}
