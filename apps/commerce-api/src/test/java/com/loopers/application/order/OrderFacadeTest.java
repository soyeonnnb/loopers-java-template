package com.loopers.application.order;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderFacadeTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문할 때")
    @Nested
    class Order {
        @DisplayName("사용자 ID가 null이면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIdIsNull() {
            // arrange
            OrderV1Dto.OrderRequest request = Instancio.create(OrderV1Dto.OrderRequest.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.order(null, request));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.UNAUTHORIZED),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 ID 정보가 없습니다.")
            );
        }

        @DisplayName("사용자 ID에 해당하는 유저가 없으면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenInvalidUserId() {
            // arrange
            OrderV1Dto.OrderRequest request = Instancio.create(OrderV1Dto.OrderRequest.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.order("la28s5d", request));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.UNAUTHORIZED),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 정보가 없습니다.")
            );
        }

        @DisplayName("상품 ID에 해당하는 상품이 없으면 404 에러가 발생한다.")
        @Test
        void throws404Exception_whenProductDoesNotExists() {
            // arrange
            String loginId = "la28s5d";
            userRepository.save(
                    Instancio.of(UserEntity.class)
                            .set(field(UserEntity::getId), null)
                            .set(field(UserEntity::getLoginId), loginId)
                            .create());
            OrderV1Dto.OrderRequest request = new OrderV1Dto.OrderRequest(List.of(new OrderV1Dto.ProductOrderRequest(9999L, 100L)), 100L, null);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.order(loginId, request));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.NOT_FOUND),
                    () -> assertEquals(exception.getCustomMessage(), "상품 정보가 없습니다.")
            );
        }
    }

    @DisplayName("사용자의 주문 리스트를 조회할 때,")
    @Nested
    class GetUserInfoList {

        @DisplayName("사용자가 null이면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIdIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserInfoList(null, null, null, 0, 1));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.UNAUTHORIZED),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 ID 정보가 없습니다.")
            );
        }


        @DisplayName("검색 시작 날짜가 검색 마지막 날짜 이후면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenInvalidDateRange() {
            // arrange
            String loginId = "la28s5d";
            userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId).create());

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserInfoList(loginId, LocalDate.of(2025, 1, 1), LocalDate.of(2024, 1, 1), 0, 1));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.")
            );
        }
    }


}
