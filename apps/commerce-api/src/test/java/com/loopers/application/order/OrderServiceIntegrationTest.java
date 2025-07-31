package com.loopers.application.order;

import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderItemEntity;
import com.loopers.domain.order.OrderItemRepository;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.product.*;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
    private OrderItemRepository orderItemRepository;

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

    @Autowired
    private OrderFacade orderFacade;


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


    }

    @DisplayName("사용자의 주문 리스트를 조회할 때,")
    @Nested
    class GetUserOrderList {

        @DisplayName("정상적으로 검색이 된다.")
        @Test
        void success_whenValidParameter() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId).create());
            UserEntity otherUserEntity = userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId + "1").create());

            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            List<ProductEntity> productEntityList = productRepository.saveAll(Instancio.ofList(ProductEntity.class)
                    .size(20)
                    .set(field(ProductEntity::getBrand), brandEntity)
                    .set(field(ProductEntity::getId), null)
                    .set(field(ProductEntity::getProductCount), null)
                    .create());
            for (int i = 0; i < 20; i++) {
                ProductEntity productEntity = productEntityList.get(i);
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            }

            for (int i = 0; i < 20; i++) {
                OrderEntity order = orderRepository.save(new OrderEntity(userEntity, 100L));
                OrderEntity otherOrder = orderRepository.save(new OrderEntity(otherUserEntity, 100L));
                for (int j = 0; j < (i % 5) + 1; j++) {
                    int productIndex = i % productEntityList.size();
                    Long quantity = (long) (j + 1);
                    orderItemRepository.save(new OrderItemEntity(order, productEntityList.get(productIndex), quantity));
                    orderItemRepository.save(new OrderItemEntity(otherOrder, productEntityList.get(productIndex), quantity));
                }
            }


            // act
            Page<OrderEntity> result = orderService.getUserOrderList(userEntity, null, null, 30, 0);

            // assert
            assertAll(
                    () -> assertEquals(1, result.getTotalPages()),
                    () -> assertEquals(20, result.getTotalElements()),
                    () -> assertEquals(20, result.getContent().size()),
                    () -> assertTrue(result.getContent().stream()
                            .allMatch(order -> order.getUser().getId().equals(userEntity.getId())))
            );
        }

        @DisplayName("사용자가 null이면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIdIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.getUserOrderList(null, null, null, 0, 1));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.UNAUTHORIZED),
                    () -> assertEquals(exception.getCustomMessage(), "사용자 정보가 없습니다.")
            );
        }


        @DisplayName("검색 시작 날짜가 검색 마지막 날짜 이후면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenInvalidDateRange() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId).create());

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderService.getUserOrderList(userEntity, LocalDate.of(2025, 1, 1), LocalDate.of(2024, 1, 1), 0, 1));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.")
            );
        }

        @DisplayName("size가 0 이하이면 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {
                -1, 0
        })
        void throws400Exception_whenSizeLessThanEquals0(int size) {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId).create());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    orderService.getUserOrderList(userEntity, null, null, size, 1)
            );

            // assert
            assertAll(
                    () -> assertEquals(result.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(result.getCustomMessage(), "페이지 크기는 최소 1 이상이여야 합니다.")
            );
        }

        @DisplayName("page가 0 미만이면 에러가 발생한다.")
        @Test
        void throws400Exception_whenPageLessThan0() {
            // arrange
            String loginId = "la28s5d";
            UserEntity userEntity = userRepository.save(Instancio.of(UserEntity.class).set(field(UserEntity::getId), null).set(field(UserEntity::getLoginId), loginId).create());

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    orderService.getUserOrderList(userEntity, null, null, 1, -1)
            );

            // assert
            assertAll(
                    () -> assertEquals(result.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(result.getCustomMessage(), "페이지는 최소 0 이상이여야 합니다.")
            );
        }
    }

    @DisplayName("사용자가 자신의 주문정보를 확인할 때,")
    @Nested
    class GetUserOrderInfo {
        private final String LOGIN_ID = "la28s5d";
        private UserEntity userEntity;
        private OrderEntity orderEntity;

        @BeforeEach
        void setup() {
            userEntity = userRepository.save(
                    Instancio.of(UserEntity.class)
                            .set(field(UserEntity::getId), null)
                            .set(field(UserEntity::getLoginId), LOGIN_ID)
                            .set(field(UserEntity::getPoint), 1000000000L)
                            .create());

            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            List<ProductEntity> productEntityList = productRepository.saveAll(Instancio.ofList(ProductEntity.class)
                    .size(5)
                    .set(field(ProductEntity::getBrand), brandEntity)
                    .set(field(ProductEntity::getId), null)
                    .set(field(ProductEntity::getProductCount), null)
                    .set(field(ProductEntity::getQuantity), 1000L)
                    .set(field(ProductEntity::getPrice), 50L)
                    .create());
            for (int i = 0; i < 5; i++) {
                ProductEntity productEntity = productEntityList.get(i);
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            }

            orderEntity = orderRepository.save(new OrderEntity(userEntity, 100L));
            List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                orderItemEntityList.add(orderItemRepository.save(new OrderItemEntity(orderEntity, productEntityList.get(i), (long) (i + 1))));
            }
            ReflectionTestUtils.setField(orderEntity, "items", orderItemEntityList);
        }

        @DisplayName("사용자 ID가 없으면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIdIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrder(null, orderEntity.getId()));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.UNAUTHORIZED, exception.getErrorType()),
                    () -> assertEquals("사용자 ID 정보가 없습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("사용자 정보가 없으면 401 에러가 발생한다.")
        @Test
        void throws401Exception_whenInvalidUserId() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrder(LOGIN_ID + "1", orderEntity.getId()));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.UNAUTHORIZED, exception.getErrorType()),
                    () -> assertEquals("사용자 정보가 없습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("주문ID가 없으면 400 에러가 발생한다.")
        @Test
        void throws400Exception_whenOrderIdIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrder(LOGIN_ID, null));


            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                    () -> assertEquals("주문 ID는 필수입니다.", exception.getCustomMessage())
            );

        }

        @DisplayName("주문ID에 해당하는 주문이 없으면 404 에러가 발생한다.")
        @Test
        void throws404Exception_whenInvalidOrderId() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrder(LOGIN_ID, orderEntity.getId() + 1L));


            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.NOT_FOUND, exception.getErrorType()),
                    () -> assertEquals("주문 정보가 없습니다.", exception.getCustomMessage())
            );

        }

        @DisplayName("사용자의 주문이 아니면 403 에러가 발생한다.")
        @Test
        void throws403Exception_whenOrderIsNotUsers() {
            // arrange
            String newLoginId = "la28s5d11";
            userRepository.save(Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .set(field(UserEntity::getLoginId), newLoginId)
                    .create()
            );

            // act
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrder(newLoginId, orderEntity.getId()));


            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.FORBIDDEN, exception.getErrorType()),
                    () -> assertEquals("다른 사용자의 주문 정보입니다.", exception.getCustomMessage())
            );

        }

        @DisplayName("사용자의 주문 번호면 정상적으로 조회된다.")
        @Test
        void success_whenValidParameter() {
            // arrange

            // act
            OrderInfo orderInfo = orderFacade.getUserOrder(LOGIN_ID, orderEntity.getId());

            // assert
            assertAll(
                    () -> assertNotNull(orderInfo),
                    () -> assertEquals(orderEntity.getId(), orderInfo.id()),
                    () -> assertEquals(userEntity.getId(), orderInfo.userInfo().id()),
                    () -> assertEquals(userEntity.getEmail(), orderInfo.userInfo().email()),
                    () -> assertEquals(orderEntity.getTotalPrice(), orderInfo.totalPrice()),
                    () -> assertEquals(orderEntity.getItems().size(), orderInfo.items().size())
            );
        }
    }
}
