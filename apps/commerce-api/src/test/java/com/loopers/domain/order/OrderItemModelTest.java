package com.loopers.domain.order;

import com.loopers.domain.product.ProductEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemModelTest {

    @DisplayName("OrderItem 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("주문이 null이면 객체 생성에 실패한다.")
        @Test
        void throws400Exception_whenOrderIsNull() {
            // arrange
            ProductEntity productEntity = Instancio.create(ProductEntity.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    new OrderItemEntity(null, productEntity, 1L)
            );

            // assert
            assertAll(
                    () -> assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "주문은 필수입니다.")
            );

        }

        @DisplayName("상품이 null이면 객체 생성에 실패한다.")
        @Test
        void throws400Exception_whenProductIsNull() {
            // arrange
            OrderEntity orderEntity = Instancio.create(OrderEntity.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    new OrderItemEntity(orderEntity, null, 1L)
            );

            // assert

            assertAll(
                    () -> assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "상품은 필수입니다.")
            );
        }


        @DisplayName("수량이 null이면 객체 생성에 실패한다.")
        @Test
        void throws400Exception_whenQuantityIsNull() {
            // arrange
            OrderEntity orderEntity = Instancio.create(OrderEntity.class);
            ProductEntity productEntity = Instancio.create(ProductEntity.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    new OrderItemEntity(orderEntity, productEntity, null)
            );

            // assert

            assertAll(
                    () -> assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "수량은 필수입니다.")
            );
        }

        @DisplayName("수량이 0 미만이면 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                -1L, 0
        })
        void throws400Exception_whenQuantityLessThan0(Long quantity) {
            // arrange
            OrderEntity orderEntity = Instancio.create(OrderEntity.class);
            ProductEntity productEntity = Instancio.create(ProductEntity.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () ->
                    new OrderItemEntity(orderEntity, productEntity, quantity)
            );

            // assert

            assertAll(
                    () -> assertThat(exception.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "수량은 1 이상이여야 합니다.")
            );
        }
    }

}
