package com.loopers.domain.user;

import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.PointErrorType;
import com.loopers.support.error.UserErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private UserFacade userFacade;

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
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest("la28s5d", "la28s5d@naver.com", "password", "FEMALE", "2025-01-01", "소연", "김소연");

            // act
            UserEntity result = userService.register(request);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertThat(result.getLoginId()).isEqualTo(request.loginId());
                    },
                    () -> {
                        assert result != null;
                        assertThat(result.getEmail()).isEqualTo(request.email());
                    },
                    () -> {
                        assert result != null;
                        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
                    },
                    () -> {
                        assert result != null;
                        assertThat(result.getBirthDate()).isEqualTo(request.birthDate());
                    },
                    () -> {
                        assert result != null;
                        assertThat(result.getNickname()).isEqualTo(request.nickname());
                    }
            );
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        void throwsException_whenAlreadyRegisterId() {
            // arrange
            String loginId = "la28s5d";
            userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            UserV1Dto.UserRegisterRequest request = new UserV1Dto.UserRegisterRequest(loginId, "la28s5d@naver.com", "password", "FEMALE", "2025-01-01", "소연", "김소연");

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

    @DisplayName("내 정보를 조회할 때,")
    @Nested
    class GetUserInfo {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void returnUserInfo_whenUserExists() {
            String loginId = "la28s5d";
            UserEntity userEntity = new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE");
            userRepository.save(userEntity);

            // act
            Optional<UserEntity> result = userService.getUserInfo(loginId);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result.isPresent();
                        assertEquals(userEntity.getLoginId(), result.get().getLoginId());
                    },
                    () -> {
                        assert result.isPresent();
                        assertEquals(userEntity.getEmail(), result.get().getEmail());
                    },
                    () -> {
                        assert result.isPresent();
                        assertEquals(userEntity.getGender(), result.get().getGender());
                    },
                    () -> {
                        assert result.isPresent();
                        assertEquals(userEntity.getBirthDate(), result.get().getBirthDate());
                    }
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnNull_whenUserNotExists() {
            String loginId = "la28s5d";

            // act
            Optional<UserEntity> result = userService.getUserInfo(loginId);

            // assert
            assertAll(
                    () -> assertTrue(result.isEmpty())
            );
        }
    }

    @DisplayName("포인트 조회를 할 때,")
    @Nested
    class GetPoint {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnPoint_whenUserExists() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE");
            userRepository.save(userEntity);

            // act  ₩₩₩₩
            Long result = userFacade.getUserPoint(loginId);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertEquals(userEntity.getPoint(), result)
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnNull_whenUserDoesNotExist() {
            // arrange
            String loginId = "la28s5d";

            // act
            Long result = userFacade.getUserPoint(loginId);

            // assert
            assertAll(
                    () -> assertThat(result).isNull()
            );
        }
    }

    @DisplayName("포인트 충전을 할 때,")
    @Nested
    class ChargePoint {
        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void throwError_whenDoesNotExistUserId() {
            // arrange

            // act
            PointV1Dto.ChargePointRequest request = new PointV1Dto.ChargePointRequest(1000L);
            CoreException result = assertThrows(CoreException.class, () ->
                    userService.chargePoint("test", request.point())
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(UserErrorType.USER_NOT_EXISTS)
            );
        }

        @DisplayName("0 이하의 금액으로 충전을 시도한 경우, 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                -100L, 0, -1L

        })
        void throwError_whenChargePointLessThan0(Long point) {
            // arrange
            UserEntity user = new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE");
            userRepository.save(user);

            // act
            PointV1Dto.ChargePointRequest request = new PointV1Dto.ChargePointRequest(point);
            CoreException result = assertThrows(CoreException.class, () ->
                    userService.chargePoint("la28s5d", request.point())
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(PointErrorType.POINT_MUST_BE_GREATER_THAN_0)
            );
        }
    }

}
