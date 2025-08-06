package com.loopers.domain.order;

import com.loopers.application.order.OrderCommand;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfo;
import com.loopers.domain.coupon.CouponEntity;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.coupon.UserCouponRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("주문할 때,")
    @Nested
    class Order {

        private final Long TOTAL_PRICE = 5000L;
        private List<OrderCommand.OrderProduct> itemList;

        private UserEntity userEntity;

        @BeforeEach
        void setup() {
            userEntity = userRepository.save(Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .set(field(UserEntity::getPoint), 10000000L)
                    .create());
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            List<ProductEntity> productEntityList = productRepository.saveAll(
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

        @Nested
        @DisplayName("쿠폰 없이 주문할 때")
        class WithoutCoupon {

            @DisplayName("주문에 성공한다.")
            @Test
            void success_order() {
                // arrange

                // act
                OrderEntity orderEntity = orderService.order(userEntity, itemList, TOTAL_PRICE, null);

                // assert
                assertAll(
                        () -> assertNotNull(orderEntity),
                        () -> assertEquals(userEntity.getId(), orderEntity.getUser().getId()),
                        () -> assertEquals(itemList.size(), orderEntity.getItems().size()),
                        () -> assertEquals(TOTAL_PRICE, orderEntity.getTotalPrice())
                );
            }

            @DisplayName("User 객체가 없으면 401 에러가 발생한다.")
            @Test
            void throws401Exception_whenUserIsNull() {
                // arrange

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(null, itemList, TOTAL_PRICE, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.UNAUTHORIZED, exception.getErrorType()),
                        () -> assertEquals("사용자 정보가 없습니다.", exception.getCustomMessage())
                );
            }

            @DisplayName("상품 리스트가 비어있으면 400 에러가 발생한다.")
            @Test
            void throws400Exception_whenOrderProductListIsEmpty() {
                // arrange

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, new ArrayList<>(), TOTAL_PRICE, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                        () -> assertEquals("주문하려는 상품은 1개 이상이여야 합니다.", exception.getCustomMessage())
                );
            }

            @DisplayName("주문 금액이 0 미만이면 400 에러가 발생한다.")
            @Test
            void throws400Exception_whenOrderPriceIsLessThan0() {
                // arrange

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, -1L, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                        () -> assertEquals("주문하려는 금액은 0 이상이여야 합니다.", exception.getCustomMessage())
                );
            }


            @DisplayName("상품이 판매중 상태가 아니면 409 에러가 발생한다.")
            @Test
            void throws400Exception_whenProductListIsNotSale() {
                // arrange
                ReflectionTestUtils.setField(itemList.getFirst().productEntity(), "status", ProductStatus.END);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                        () -> assertEquals("상품이 판매중 상태가 아닙니다.", exception.getCustomMessage())
                );
            }

            @DisplayName("상품 금액과 주문 금액이 일치하지 않으면 400 에러가 발생한다.")
            @Test
            void throws400Exception_whenTotalPriceIsInvalid() {
                // arrange

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE - 200L, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                        () -> assertEquals("상품 총 합계와 주문 금액이 일치하지 않습니다.", exception.getCustomMessage())
                );
            }

            @DisplayName("사용자 포인트가 부족하면 409 에러가 발생한다.")
            @Test
            void throws409Exception_whenUserPointIsNotEnough() {
                // arrange
                ReflectionTestUtils.setField(userEntity, "point", 200L);

                // act
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                        () -> assertEquals("사용자 포인트는 0 미만이 될 수 없습니다.", exception.getCustomMessage())
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
                CoreException exception = assertThrows(CoreException.class, () -> orderService.order(userEntity, itemList, TOTAL_PRICE, null));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                        () -> assertEquals("상품 재고가 부족합니다.", exception.getCustomMessage())
                );
            }


        }

        @Nested
        @DisplayName("쿠폰으로 주문할 때")
        class WithCoupon {

            private UserCouponEntity userFlatCoupon;
            private UserCouponEntity userRateCoupon;
            private CouponEntity flatCoupon;
            private CouponEntity rateCoupon;

            @BeforeEach
            void setup() {
                ZonedDateTime expiredAt = ZonedDateTime.now().plusDays(12);
                flatCoupon = couponRepository.save(new CouponEntity("정액 쿠폰", "FLAT", 3000L, 200L, null, expiredAt));
                rateCoupon = couponRepository.save(new CouponEntity("정률 쿠폰", "RATE", 3000L, 2500L, 10.0, expiredAt));
                userFlatCoupon = userCouponRepository.save(new UserCouponEntity(userEntity, flatCoupon, expiredAt, null, null));
                ReflectionTestUtils.setField(userFlatCoupon, "coupon", flatCoupon);
                userRateCoupon = userCouponRepository.save(new UserCouponEntity(userEntity, rateCoupon, expiredAt, null, null));
                ReflectionTestUtils.setField(userRateCoupon, "coupon", rateCoupon);
            }

            @DisplayName("정액쿠폰일 때, 주문에 성공한다.")
            @Test
            void successOrder_whenFlatCoupon() {
                // arrange
                Long salePrice = TOTAL_PRICE - flatCoupon.getMaxUsePrice();

                // act
                OrderEntity orderEntity = orderService.order(userEntity, itemList, salePrice, userFlatCoupon);

                // assert
                assertAll(
                        () -> assertNotNull(orderEntity),
                        () -> assertEquals(userEntity.getId(), orderEntity.getUser().getId()),
                        () -> assertEquals(itemList.size(), orderEntity.getItems().size()),
                        () -> assertEquals(salePrice, orderEntity.getTotalPrice()),
                        () -> assertNotNull(userFlatCoupon.getUsedAt()),
                        () -> assertEquals(orderEntity.getUserCoupon().getId(), userFlatCoupon.getId())
                );
            }

            @DisplayName("정률쿠폰일 때, 주문에 성공한다.")
            @Test
            void successOrder_whenRateCoupon() {
                // arrange
                BigDecimal price = BigDecimal.valueOf(TOTAL_PRICE);
                BigDecimal rate = BigDecimal.valueOf(rateCoupon.getRate());
                BigDecimal discount = price.multiply(rate).divide(BigDecimal.valueOf(100));
                Long salePrice = price.subtract(discount.min(BigDecimal.valueOf(rateCoupon.getMaxUsePrice()))).longValue();

                // act
                OrderEntity orderEntity = orderService.order(userEntity, itemList, salePrice, userRateCoupon);

                // assert
                assertAll(
                        () -> assertNotNull(orderEntity),
                        () -> assertEquals(userEntity.getId(), orderEntity.getUser().getId()),
                        () -> assertEquals(itemList.size(), orderEntity.getItems().size()),
                        () -> assertEquals(salePrice, orderEntity.getTotalPrice()),
                        () -> assertNotNull(userRateCoupon.getUsedAt()),
                        () -> assertEquals(orderEntity.getUserCoupon().getId(), userRateCoupon.getId())
                );
            }

            @DisplayName("정액 쿠폰의 가격이 일치하지 않으면, 400 에러가 발생한다. ")
            @Test
            void throws400Exception_whenFlatCouponPriceIsInvalid() {
                // arrange
                Long salePrice = TOTAL_PRICE - flatCoupon.getMaxUsePrice() + 100L;

                // act
                CoreException exception = assertThrows(CoreException.class, () ->
                        orderService.order(userEntity, itemList, salePrice, userFlatCoupon));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                        () -> assertEquals("상품 총 합계와 주문 금액이 일치하지 않습니다.", exception.getCustomMessage())
                );
            }

            @DisplayName("정률 쿠폰의 가격이 일치하지 않으면, 400 에러가 발생한다. ")
            @Test
            void throws400Exception_whenRateCouponPriceIsInvalid() {
                // arrange
                BigDecimal price = BigDecimal.valueOf(TOTAL_PRICE);
                BigDecimal rate = BigDecimal.valueOf(rateCoupon.getRate());
                BigDecimal discount = price.multiply(rate).divide(BigDecimal.valueOf(100));
                Long salePrice = price.subtract(discount.min(BigDecimal.valueOf(rateCoupon.getMaxUsePrice()))).longValue() + 100L;

                // act
                CoreException exception = assertThrows(CoreException.class, () ->
                        orderService.order(userEntity, itemList, salePrice, userFlatCoupon));

                // assert
                assertAll(
                        () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                        () -> assertEquals("상품 총 합계와 주문 금액이 일치하지 않습니다.", exception.getCustomMessage())
                );
            }
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
                OrderEntity order = orderRepository.save(new OrderEntity(userEntity, 100L, null));
                OrderEntity otherOrder = orderRepository.save(new OrderEntity(otherUserEntity, 100L, null));
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
                    () -> assertEquals(GlobalErrorType.UNAUTHORIZED, exception.getErrorType()),
                    () -> assertEquals("사용자 정보가 없습니다.", exception.getCustomMessage())
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
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                    () -> assertEquals("검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.", exception.getCustomMessage())
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
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, result.getErrorType()),
                    () -> assertEquals("페이지 크기는 최소 1 이상이여야 합니다.", result.getCustomMessage())
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
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, result.getErrorType()),
                    () -> assertEquals("페이지는 최소 0 이상이여야 합니다.", result.getCustomMessage())
            );
        }
    }

    @DisplayName("사용자가 자신의 주문정보를 확인할 때,")
    @Nested
    class GetUserOrderInfo {
        private final String LOGIN_ID = "la28s5d";
        private UserEntity userEntity;
        private OrderEntity orderEntity;

        private UserCouponEntity userFlatCoupon;
        private UserCouponEntity userRateCoupon;
        private CouponEntity flatCoupon;
        private CouponEntity rateCoupon;

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

            ZonedDateTime expiredAt = ZonedDateTime.now().plusDays(12);
            flatCoupon = couponRepository.save(new CouponEntity("정액 쿠폰", "FLAT", 3000L, 200L, null, expiredAt));
            userFlatCoupon = userCouponRepository.save(new UserCouponEntity(userEntity, flatCoupon, expiredAt, null, null));
            ReflectionTestUtils.setField(userFlatCoupon, "coupon", flatCoupon);

            orderEntity = orderRepository.save(new OrderEntity(userEntity, 100L, userFlatCoupon));
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

        @DisplayName("사용자의 주문 번호면 정상적으로 조회된다. (쿠폰 존재)")
        @Test
        void success_whenValidParameterWithCoupon() {
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
                    () -> assertEquals(orderEntity.getItems().size(), orderInfo.items().size()),
                    () -> assertEquals(userFlatCoupon.getId(), orderInfo.userCouponInfo().id()),
                    () -> assertEquals(userFlatCoupon.getBeforePrice(), orderInfo.userCouponInfo().beforePrice()),
                    () -> assertTrue(userFlatCoupon.getExpiredAt().isEqual(orderInfo.userCouponInfo().expiredAt())),
                    () -> assertEquals(flatCoupon.getId(), orderInfo.userCouponInfo().coupon().id()),
                    () -> assertEquals(flatCoupon.getMinOrderPrice(), orderInfo.userCouponInfo().coupon().minOrderPrice())
            );
        }

        @DisplayName("사용자의 주문 번호면 정상적으로 조회된다. (쿠폰 미존재)")
        @Test
        void success_whenValidParameterWithoutCoupon() {
            // arrange
            ReflectionTestUtils.setField(orderEntity, "userCoupon", null);

            // act
            OrderInfo orderInfo = orderFacade.getUserOrder(LOGIN_ID, orderEntity.getId());

            // assert
            assertAll(
                    () -> assertNotNull(orderInfo),
                    () -> assertEquals(orderEntity.getId(), orderInfo.id()),
                    () -> assertEquals(userEntity.getId(), orderInfo.userInfo().id()),
                    () -> assertEquals(userEntity.getEmail(), orderInfo.userInfo().email()),
                    () -> assertEquals(orderEntity.getTotalPrice(), orderInfo.totalPrice()),
                    () -> assertEquals(orderEntity.getItems().size(), orderInfo.items().size()),
                    () -> assertNull(orderEntity.getUserCoupon())
            );
        }
    }
}


