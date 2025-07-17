package com.loopers.interfaces.api;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.support.error.UserErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    private final UserRepository userRepository;

    @Autowired
    public PointV1ApiE2ETest(
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

    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPoints {

        private static final String ENDPOINT = "/api/v1/points";

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnPoint_whenValidUser() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "F");
//            given(userRepository.findByLoginId("la28s5d")).willReturn(Optional.of(userEntity));
            userRepository.save(userEntity);

            // act
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", loginId);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.GET,  new HttpEntity<>(headers), responseType);

            // assert
            assertAll(  () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertNotNull(response.getBody().data()),
                    () -> assertThat(response.getBody().data().point()).isEqualTo(0L)
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throw400Error_whenXUserIdIsNull() {
            // arrange

            // act
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.GET,  new HttpEntity<>(null), responseType);

            // assert
            assertAll(  () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getBody().meta().errorCode()).isEqualTo(GlobalErrorType.BAD_REQUEST.getCode())
            );
        }
    }

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class ChargePoints {

        private static final String ENDPOINT = "/api/v1/points/charge";

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnTotalPoint_whenExistsUserCharge1000won() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "F");
            userRepository.save(userEntity);

            // act
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", loginId);

            PointV1Dto.ChargePointRequest request = new PointV1Dto.ChargePointRequest(1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.POST,  new HttpEntity<>(request, headers), responseType);

            // assert
            assertAll(  () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertNotNull(response.getBody()),
                    () -> assertNotNull(response.getBody().data()),
                    () -> assertThat(response.getBody().data().point()).isEqualTo(1000L)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void throw404Error_whenUserDoesNotExists() {
            // arrange

            // act
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", "la28s5d");

            PointV1Dto.ChargePointRequest request = new PointV1Dto.ChargePointRequest(1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(ENDPOINT, HttpMethod.POST,  new HttpEntity<>(request, headers), responseType);

            // assert
            assertAll(  () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)),
                    () -> assertThat(response.getBody().meta().errorCode()).isEqualTo(UserErrorType.USER_NOT_EXISTS.getCode())
            );
        }
    }
}
