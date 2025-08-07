package com.loopers.application.order;

import com.loopers.domain.coupon.CouponEntity;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.coupon.UserCouponRepository;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.product.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderFacadeTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private ProductCountRepository productCountRepository;

    @Autowired
    private BrandRepository brandRepository;


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

        @DisplayName("동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다.")
        @Test
        void successOnce_whenSameCoupon() throws InterruptedException {
            // arrange
            UserEntity userEntity = userRepository.save(
                    Instancio.of(UserEntity.class)
                            .set(field(UserEntity::getId), null)
                            .set(field(UserEntity::getPoint), 1000000000L)
                            .create());

            ZonedDateTime expiredAt = ZonedDateTime.now().plusDays(12);
            CouponEntity flatCoupon = couponRepository.save(new CouponEntity("정액 쿠폰", "FLAT", 3000L, 200L, null, expiredAt));

            UserCouponEntity userFlatCoupon = userCouponRepository.save(new UserCouponEntity(userEntity, flatCoupon, expiredAt, null, null));
            ReflectionTestUtils.setField(userFlatCoupon, "coupon", flatCoupon);
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = Instancio.of(ProductEntity.class)
                    .set(field(ProductEntity::getBrand), brandEntity)
                    .set(field(ProductEntity::getId), null)
                    .set(field(ProductEntity::getProductCount), null)
                    .set(field(ProductEntity::getQuantity), 1000L)
                    .set(field(ProductEntity::getStatus), ProductStatus.SALE)
                    .set(field(ProductEntity::getPrice), 5000L)
                    .create();
            ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 0L);
            ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            productEntity = productRepository.save(productEntity);

            List<OrderV1Dto.ProductOrderRequest> items = List.of(new OrderV1Dto.ProductOrderRequest(productEntity.getId(), 1L));
            OrderV1Dto.OrderRequest request = new OrderV1Dto.OrderRequest(items, 4800L, userFlatCoupon.getId());

            // act
            int SIZE = 50;
            ExecutorService executor = Executors.newFixedThreadPool(SIZE);
            CountDownLatch latch = new CountDownLatch(SIZE);

            for (int i = 0; i < SIZE; i++) {
                executor.submit(() -> {
                    try {
                        orderFacade.order(userEntity.getLoginId(), request);
                    } catch (Exception e) {
                        System.out.println("실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // assert
            ProductEntity finalProductEntity = productEntity;
            assertAll(
                    () -> {
                        UserCouponEntity newUserCouponEntity = userCouponRepository.findById(userFlatCoupon.getId()).orElse(null);
                        assertNotNull(newUserCouponEntity);
                        assertNotNull(newUserCouponEntity.getUsedAt());
                    },
                    () -> {
                        UserEntity newUserEntity = userRepository.findByLoginId(userEntity.getLoginId()).orElse(null);
                        assertNotNull(newUserEntity);
                        assertEquals(1000000000L - 4800L, newUserEntity.getPoint());
                    },
                    () -> {
                        ProductEntity newProductEntity = productRepository.findById(finalProductEntity.getId()).orElse(null);
                        assertNotNull(newProductEntity);
                        assertEquals(999L, newProductEntity.getQuantity());
                    },
                    () -> {
                        int size = orderRepository.findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(userEntity, ZonedDateTime.now().minusDays(10L), ZonedDateTime.now().plusDays(10L), PageRequest.of(0, 1000)).getNumberOfElements();
                        assertEquals(1, size);
                    }
            );
        }

        @DisplayName("동일한 유저가 여러 기기에서 동시에 주문에도, 포인트가 중복 차감되지 않아야 한다.")
        @Test
        void successPointUse_whenConcurrencyOrder() throws InterruptedException {
            // arrange
            UserEntity userEntity = userRepository.save(
                    Instancio.of(UserEntity.class)
                            .set(field(UserEntity::getId), null)
                            .set(field(UserEntity::getPoint), 1000000000L)
                            .create());

            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = Instancio.of(ProductEntity.class)
                    .set(field(ProductEntity::getBrand), brandEntity)
                    .set(field(ProductEntity::getId), null)
                    .set(field(ProductEntity::getProductCount), null)
                    .set(field(ProductEntity::getQuantity), 10000L)
                    .set(field(ProductEntity::getStatus), ProductStatus.SALE)
                    .set(field(ProductEntity::getPrice), 500L)
                    .create();
            ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 0L);
            ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            productEntity = productRepository.save(productEntity);

            List<OrderV1Dto.OrderRequest> requestList = new ArrayList<>();
            int SIZE = 50;
            int totalQuantity = 0;
            for (int i = 1; i <= SIZE; i++) {
                List<OrderV1Dto.ProductOrderRequest> items = List.of(new OrderV1Dto.ProductOrderRequest(productEntity.getId(), (long) i));
                requestList.add(new OrderV1Dto.OrderRequest(items, 500L * i, null));
                totalQuantity += i;
            }


            // act
            ExecutorService executor = Executors.newFixedThreadPool(SIZE);
            CountDownLatch latch = new CountDownLatch(SIZE);

            for (int i = 0; i < SIZE; i++) {
                int finalI = i;
                executor.submit(() -> {
                    try {
                        orderFacade.order(userEntity.getLoginId(), requestList.get(finalI));
                    } catch (Exception e) {
                        System.out.println("실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // assert
            ProductEntity finalProductEntity = productEntity;
            int finalTotalQuantity = totalQuantity;
            assertAll(
                    () -> {
                        UserEntity newUserEntity = userRepository.findByLoginId(userEntity.getLoginId()).orElse(null);
                        assertNotNull(newUserEntity);
                        assertEquals(1000000000L - finalTotalQuantity * 500L, newUserEntity.getPoint());
                    },
                    () -> {
                        ProductEntity newProductEntity = productRepository.findById(finalProductEntity.getId()).orElse(null);
                        assertNotNull(newProductEntity);
                        assertEquals(10000L - finalTotalQuantity, newProductEntity.getQuantity());
                    },
                    () -> {
                        int size = orderRepository.findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(userEntity, ZonedDateTime.now().minusDays(10L), ZonedDateTime.now().plusDays(10L), PageRequest.of(0, 1000)).getNumberOfElements();
                        assertEquals(SIZE, size);
                    }
            );
        }

        @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.")
        @Test
        void successQuantityUse_whenConcurrencyOrder() throws InterruptedException {
            // arrange
            int SIZE = 200;
            final long userDefaultPoint = 15000000L;
            final long productDefaultQuantity = 30000L;
            List<UserEntity> userEntityList = new ArrayList<>();
            for (int i = 0; i < SIZE; i++) {
                userEntityList.add(userRepository.save(Instancio.of(UserEntity.class)
                        .set(field(UserEntity::getId), null)
                        .set(field(UserEntity::getPoint), userDefaultPoint)
                        .create()));
            }

            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = Instancio.of(ProductEntity.class)
                    .set(field(ProductEntity::getBrand), brandEntity)
                    .set(field(ProductEntity::getId), null)
                    .set(field(ProductEntity::getProductCount), null)
                    .set(field(ProductEntity::getQuantity), productDefaultQuantity)
                    .set(field(ProductEntity::getStatus), ProductStatus.SALE)
                    .set(field(ProductEntity::getPrice), 500L)
                    .create();
            ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 0L);
            ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            productEntity = productRepository.save(productEntity);

            List<OrderV1Dto.OrderRequest> requestList = new ArrayList<>();

            int totalQuantity = 0;
            for (int i = 0; i < SIZE; i++) {
                int orderCount = i + 1;
                long orderPrice = 500L * orderCount;
                List<OrderV1Dto.ProductOrderRequest> items = List.of(new OrderV1Dto.ProductOrderRequest(productEntity.getId(), (long) orderCount));
                requestList.add(new OrderV1Dto.OrderRequest(items, orderPrice, null));
                totalQuantity += orderCount;
            }

            // act
            ExecutorService executor = Executors.newFixedThreadPool(SIZE);
            CountDownLatch latch = new CountDownLatch(SIZE);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            for (int i = 0; i < SIZE; i++) {
                int finalI = i;
                executor.submit(() -> {
                    try {
                        orderFacade.order(userEntityList.get(finalI).getLoginId(), requestList.get(finalI));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.out.println("실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();

            // assert
            ProductEntity finalProductEntity = productEntity;
            long finalTotalQuantity = totalQuantity;
            assertAll(
                    // 1. 모든 주문이 성공했는지 확인
                    () -> assertThat(successCount.get()).isEqualTo(SIZE),
                    () -> assertThat(failureCount.get()).isEqualTo(0),

                    // 2. 상품 재고가 정확히 차감되었는지 확인
                    () -> {
                        ProductEntity updatedProduct = productRepository.findById(finalProductEntity.getId())
                                .orElseThrow();
                        assertEquals(productDefaultQuantity - finalTotalQuantity, updatedProduct.getQuantity());
                    },

                    // 3. 각 사용자의 포인트가 정확히 차감되었는지 확인
                    () -> {
                        for (int i = 0; i < SIZE; i++) {
                            UserEntity updatedUser = userRepository.findByLoginId(userEntityList.get(i).getLoginId())
                                    .orElseThrow(() -> new AssertionError("사용자를 찾을 수 없습니다"));
                            long expectedPoint = userDefaultPoint - (500L * (i + 1));
                            assertThat(updatedUser.getPoint()).isEqualTo(expectedPoint);
                        }
                    },

                    // 4. 테스트 기간 동안 생성된 주문 개수 확인
                    () -> {
                        long totalOrders = 0;
                        for (UserEntity user : userEntityList) {
                            totalOrders += orderRepository.findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(
                                    user,
                                    ZonedDateTime.now().minusDays(1L),
                                    ZonedDateTime.now().plusDays(1L),
                                    PageRequest.of(0, 1000)
                            ).getNumberOfElements();
                        }
                        assertThat(totalOrders).isEqualTo(SIZE);
                    }
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
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrderInfoList(null, null, null, 0, 1));

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
            CoreException exception = assertThrows(CoreException.class, () -> orderFacade.getUserOrderInfoList(loginId, LocalDate.of(2025, 1, 1), LocalDate.of(2024, 1, 1), 0, 1));

            // assert
            assertAll(
                    () -> assertEquals(exception.getErrorType(), GlobalErrorType.BAD_REQUEST),
                    () -> assertEquals(exception.getCustomMessage(), "검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.")
            );
        }
    }


}
