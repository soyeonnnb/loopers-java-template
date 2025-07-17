package com.loopers.interfaces.api;

import com.loopers.domain.example.ExampleModel;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.infrastructure.example.ExampleJpaRepository;
import com.loopers.infrastructure.user.UserJpaRepository;
import com.loopers.interfaces.api.example.ExampleV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.support.error.UserErrorType;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest {


    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final UserRepository userRepository;

    @Autowired
    public UserV1ApiE2ETest(
        TestRestTemplate testRestTemplate,
        DatabaseCleanUp databaseCleanUp,
        UserRepository userRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.userRepository = userRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class Register {

        private static final String ENDPOINT = "/api/v1/users";

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnUserInfo_whenValidInfoProvided() {
            // arrange
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest(
                   "la28s5d", "la28s5d@naver.com", "password", "F",  "2025-01-01", "소연", "김소연"
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                testRestTemplate.exchange(ENDPOINT, HttpMethod.POST,  new HttpEntity<>(request), responseType);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().loginId()).isEqualTo(request.loginId()),
                () -> assertThat(response.getBody().data().email()).isEqualTo(request.email()),
                () -> assertThat(response.getBody().data().birthDate()).isEqualTo(request.birthDate()),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(request.gender())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throw400Error_whenGenderIsNull() {
            // arrange
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest(
                    "la28s5d", "la28s5d@naver.com", "password", "",  "2025-01-01", "소연", "김소연"
            );

            // act
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.POST,  new HttpEntity<>(request), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getBody().meta().errorCode()).isEqualTo(UserErrorType.GENDER_CANNOT_BE_NULL.getCode())
            );
        }

    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GetUserInfo {

        private static final String ENDPOINT = "/api/v1/users/me";

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnUserInfo_whenUserExists() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "F");
            userRepository.save(userEntity);

            // act
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", loginId);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.GET,  new HttpEntity<>(headers), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo(userEntity.getLoginId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(userEntity.getEmail()),
                    () -> assertThat(response.getBody().data().birthDate()).isEqualTo(userEntity.getBirthDate()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(userEntity.getGender())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void return404NotFound_whenUserNotFound() {
            // arrange
            String loginId = "la28s5d";

            // act
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", loginId);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.GET,  new HttpEntity<>(headers), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND))
            );
        }
    }
}
