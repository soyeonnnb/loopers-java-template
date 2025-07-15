package com.loopers.domain.user;

import com.loopers.domain.example.ExampleModel;
import com.loopers.domain.example.ExampleService;
import com.loopers.infrastructure.example.ExampleJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.support.error.UserErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원가입을 할 때,")
    @Nested
    class Register {
        @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
        @Test
        void saveUserEntity_whenUserRegister() {
            // arrange
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest("la28s5d", "la28s5d@naver.com", "password", "F", "2025-01-01", "소연");

            // act
            UserEntity result = userService.register(request);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getLoginId()).isEqualTo(request.loginId()),
                    () -> assertThat(result.getEmail()).isEqualTo(request.email()),
                    () -> assertThat(result.getGender()).isEqualTo(request.gender()),
                    () -> assertThat(result.getBirthDate()).isEqualTo(request.birthDate()),
                    () -> assertThat(result.getNickname()).isEqualTo(request.nickname())
//                    () -> verify(userJpaRepository).save(result)
            );
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        void throwsException_whenAlreadyRegisterId() {
            // arrange
            String loginId = "la28s5d";
            userJpaRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "F"));
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest(loginId, "la28s5d@naver.com", "password", "F", "2025-01-01", "소연");

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    userService.register(request)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(UserErrorType.DUPLICATE_LOGIN_ID)
            );
        }
    }
}
