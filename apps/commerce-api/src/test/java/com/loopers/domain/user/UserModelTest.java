package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserModelTest {
    @DisplayName("User 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("ID 가 영문 및 숫자 10자 이내 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "abcdef12345",
                "abcdefghijk",
                "12345678901",
                "__123",
                "&abc123",
                "abc def",
        })
        void throwsBadRequestException_whenInvalidId(String loginId) {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "F")
            );
        }

        @DisplayName("이메일이 xx@yy.zz 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "", "xxyyzz", "xxyy.zz", "xx@yyzz", "x@y.z", "xx@y.z", "x@yy.z",
                "xx@yy.z", "@yy.zz", "xx@yy.", "xx@.zz", "xx@@yy.zz", "xx@yy.zz.",
                "x x@yy.zz", "xx@yy .zz", "xx@yy,zz", "xx@yy.zz "
        })
        void throwsBadRequestException_whenInvalidEmail(String email) {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new UserEntity("la28s5d", "password", email, "김소연", "소연", "2025-01-01", "F")
            );
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "abcd-ef-gh",
                "abcd-01-01",
                "2025-ef-01",
                "2025-01-gh",
                "2025-1-23",
                "202-01-23",
                "2025-01-2",
                "20256-01-23",
                "2025-012-34",
                "2025-01-234",
                "-01-23",
                "2025--23",
                "2025-01-",
                "2025-13-01",
                "2025-01-32"
        })
        void throwsBadRequestException_whenInvalidBirth(String birth) {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", birth, "FEMALE")
            );
        }

        @DisplayName("성별이 'FEMALE', 'MALE', 'UNKNOWN' 외에는 User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "F",
                "M",
                "Female",
                "FeMaLe",
                "female",
                "FEMALE1",
                " FEMALE",
                "FEMALE ",
                " FEMALE "

        })
        void throwsBadRequestException_whenInvalidGender(String gender) {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", gender)
            );
        }
    }
}
