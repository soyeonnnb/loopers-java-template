package com.loopers.domain.order;

import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderModelTest {
    @DisplayName("주문 상품을 넣을 때, 상품이 null이면 상품 추가에 실패한다.")
    @Test
    void throws400Exception_whenAddedOrderItemIsNull() {
        // arrange
        OrderEntity orderEntity = Instancio.create(OrderEntity.class);

        // act
        CoreException exception = assertThrows(CoreException.class, () -> {
            orderEntity.addOrderItem(null);
        });

        // assert
        assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST);
    }

    @DisplayName("Order 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("사용자가 null이면 객체 생성에 실패한다.")
        @Test
        void throws401Exception_whenUserIsNull() {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new OrderEntity(null, 1L)
            );
        }

        @DisplayName("금액이 0 미만이면 객체 생성에 실패한다.")
        @Test
        void throws400Exception_whenPriceLessThan0() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);

            // act & assert
            assertThrows(CoreException.class, () ->
                    new OrderEntity(user, -1L)
            );
        }
    }

}
