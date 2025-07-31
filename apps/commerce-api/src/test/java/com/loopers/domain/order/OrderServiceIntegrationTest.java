package com.loopers.domain.order;

import com.loopers.application.order.OrderCommand;
import com.loopers.application.order.OrderService;
import com.loopers.domain.product.*;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCountRepository productCountRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문할 때,")
    @Nested
    class Order {

        private final Long TOTAL_PRICE = 5000L;
        private List<ProductEntity> productEntityList;
        private List<OrderCommand.OrderProduct> itemList;

        private UserEntity userEntity;

        @BeforeEach
        void setup() {
            userEntity = Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .set(field(UserEntity::getPoint), 10000000L)
                    .create();
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            productEntityList = productRepository.saveAll(
                    Instancio.ofList(ProductEntity.class)
                            .size(4)
                            .set(field(ProductEntity::getId), null)
                            .set(field(ProductEntity::getBrand), brandEntity)
                            .set(field(ProductEntity::getQuantity), 4L)
                            .set(field(ProductEntity::getPrice), 500L)
                            .set(field(ProductEntity::getProductCount), null)
                            .set(field(ProductEntity::getStatus), ProductStatus.SALE)
                            .create());
            itemList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ProductEntity productEntity = productEntityList.get(i);
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                itemList.add(new OrderCommand.OrderProduct(productEntity, (long) (i + 1)));
            }
        }


//        @AfterEach
//        void tearDown() {
//            databaseCleanUp.truncateAllTables();
//        }

        @DisplayName("User 객체가 없으면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(null, itemList, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.UNAUTHORIZED),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 정보가 없습니다.")
            );
        }

        @DisplayName("상품 리스트가 비어있으면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenOrderProductListIsEmpty() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, new ArrayList<>(), TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "주문하려는 상품은 1개 이상이여야 합니다.")
            );
        }

        @DisplayName("주문 금액이 0 미만이면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenOrderPriceIsLessThan0() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, -1L));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "주문하려는 금액은 0 이상이여야 합니다.")
            );
        }


        @DisplayName("상품이 판매중 상태가 아니면 409 에러가 발생한다.")
        @Test
        void throws400Exception_whenProductListIsNotSale() {
            // arrange
            ReflectionTestUtils.setField(itemList.getFirst().productEntity(), "status", ProductStatus.END);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "상품이 판매중 상태가 아닙니다.")
            );
        }

        @DisplayName("상품 금액과 주문 금액이 일치하지 않으면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenTotalPriceIsInvalid() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE - 200L));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "상품 총 합계와 주문 금액이 일치하지 않습니다.")
            );
        }

        @DisplayName("사용자 포인트가 부족하면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenUserPointIsNotEnough() {
            // arrange
            ReflectionTestUtils.setField(userEntity, "point", 200L);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE));

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

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.CONFLICT),
                    () -> assertEquals(exception.getCustomMessage(), "상품 재고가 부족합니다.")
            );
        }


        // 상품 ID에 해당하는 상품이 하나라도 없으면 404 에러가 발생한다. -> facade

    }
}
