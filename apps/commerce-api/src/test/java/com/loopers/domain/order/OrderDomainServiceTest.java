package com.loopers.domain.order;

import com.loopers.application.order.OrderCommand;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.ProductCountEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderDomainServiceTest {
    private final Long TOTAL_PRICE = 5000L;
    private List<OrderCommand.OrderProduct> itemList;

    @Autowired
    private OrderDomainService orderDomainService;

    @BeforeEach
    void setup() {
        BrandEntity brandEntity = Instancio.create(BrandEntity.class);
        List<ProductEntity> productEntityList = Instancio.ofList(ProductEntity.class)
                .size(4)
                .set(field(ProductEntity::getId), null)
                .set(field(ProductEntity::getBrand), brandEntity)
                .set(field(ProductEntity::getQuantity), 4L)
                .set(field(ProductEntity::getPrice), 500L)
                .set(field(ProductEntity::getProductCount), null)
                .set(field(ProductEntity::getStatus), ProductStatus.SALE)
                .create();
        itemList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ProductEntity productEntity = productEntityList.get(i);
            ProductCountEntity productCountEntity = new ProductCountEntity(productEntity);
            ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            itemList.add(new OrderCommand.OrderProduct(productEntity, (long) (i + 1)));
        }
    }

    @DisplayName("상품의 유효성을 확인할 때")
    @Nested
    class ValidateOrderItems {
        @DisplayName("상품이 판매중 상태가 아니면 409 에러가 발생한다.")
        @Test
        void throws400Exception_whenProductListIsNotSale() {
            // arrange
            ReflectionTestUtils.setField(itemList.getFirst().productEntity(), "status", ProductStatus.END);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderDomainService.validateOrderItems(itemList));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "상품이 판매중 상태가 아닙니다.")
            );
        }
    }

    @DisplayName("상품의 가격을 계산할 때,")
    @Nested
    class CalculateTotalPrice {
        @DisplayName("계산에 성공한다.")
        @Test
        void throws400Exception_whenOrderPriceIsLessThan0() {
            // arrange

            // act
            Long price = orderDomainService.calculateTotalPrice(itemList);

            // assert
            assertAll(
                    () -> assertEquals(price, 5000L)
            );
        }
    }

    @DisplayName("주문할 때")
    @Nested
    class ProcessOrder {
        @DisplayName("사용자 포인트가 부족하면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenUserPointIsNotEnough() {
            // arrange
            UserEntity userEntity = Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .set(field(UserEntity::getPoint), 200L)
                    .create();

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderDomainService.processOrder(userEntity, itemList, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 포인트는 0 미만이 될 수 없습니다.")
            );
        }

        @DisplayName("재고가 없으면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenQuantityIsNotEnough() {
            // arrange
            for (OrderCommand.OrderProduct orderProduct : itemList) {
                ReflectionTestUtils.setField(orderProduct.productEntity(), "quantity", 1L);
            }
            UserEntity userEntity = Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .set(field(UserEntity::getPoint), 2000000L)
                    .create();

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderDomainService.processOrder(userEntity, itemList, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "상품 재고가 부족합니다.")
            );
        }

    }
}
