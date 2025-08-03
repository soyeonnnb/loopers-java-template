package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

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

    @DisplayName("포인트를 사용할 때,")
    @Nested
    class UsePoint {
        @DisplayName("포인트가 충분하면 포인트가 사용된다.")
        @Test
        void success_whenPointIsEnough() {
            // arrange
            UserEntity userEntity = Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getPoint), 100L)
                    .create();

            // act
            userEntity.usePoint(50L);

            // assert
            assertAll(
                    () -> assertEquals(userEntity.getPoint(), 50L)
            );
        }

        @DisplayName("포인트가 부족하면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenPointIsNotEnough() {
            // arrange
            UserEntity userEntity = Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getPoint), 100L)
                    .create();

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userEntity.usePoint(500L));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 포인트는 0 미만이 될 수 없습니다."),
                    () -> assertEquals(userEntity.getPoint(), 100L)
            );
        }
    }
}
