package com.loopers.domain.coupon;

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

import java.time.ZonedDateTime;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserCouponDomainServiceTest {
    @Autowired
    private UserCouponDomainService userCouponDomainService;

    @DisplayName("사용자 쿠폰의 유효성을 확인할 때")
    @Nested
    class ValidateUseCoupon {

        private final Long TOTAL_PRICE = 5000L;
        private UserEntity user;
        private UserCouponEntity userCoupon;

        @BeforeEach
        void setup() {
            user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.of(CouponEntity.class)
                    .set(field(CouponEntity::getMinOrderPrice), 3000L)
                    .create();
            ZonedDateTime expired = ZonedDateTime.now().plusDays(12L);
            userCoupon = new UserCouponEntity(user, coupon, expired, null, null);
        }

        @DisplayName("사용자가 null이면 401에러가 발생한다.")
        @Test
        void throws401Exception_whenUserIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(null, userCoupon, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.UNAUTHORIZED, exception.getErrorType()),
                    () -> assertEquals("쿠폰 사용 시, 사용자가 필수입니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("사용자 쿠폰이 null이면 400에러가 발생한다.")
        @Test
        void throws400Exception_whenUserCouponIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(user, null, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                    () -> assertEquals("검증할 쿠폰이 없습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("금액이 null이면 400에러가 발생한다.")
        @Test
        void throws400Exception_whenPriceIsNull() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(user, userCoupon, null));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, exception.getErrorType()),
                    () -> assertEquals("검증할 금액이 없습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("사용자의 쿠폰이 아니면 403에러가 발생한다.")
        @Test
        void throws403Exception_whenNotUsersCoupon() {
            // arrange
            UserEntity otherUser = Instancio.create(UserEntity.class);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(otherUser, userCoupon, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.FORBIDDEN, exception.getErrorType()),
                    () -> assertEquals("로그인한 사용자의 쿠폰이 아닙니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("이미 사용한 쿠폰이면 409에러가 발생한다.")
        @Test
        void throws409Exception_whenAlreadyUsedCoupon() {
            // arrange
            ReflectionTestUtils.setField(userCoupon, "usedAt", ZonedDateTime.now().minusDays(1));
            ReflectionTestUtils.setField(userCoupon, "beforePrice", 3000L);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(user, userCoupon, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                    () -> assertEquals("이미 사용한 쿠폰입니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("사용 기간이 지난 쿠폰이면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenExpiredCoupon() {
            // arrange
            ReflectionTestUtils.setField(userCoupon, "expiredAt", ZonedDateTime.now().minusDays(10L));

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(user, userCoupon, TOTAL_PRICE));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                    () -> assertEquals("쿠폰 유효기간이 지났습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("최저 주문 금액을 맞추지 못한 쿠폰이면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenMinOrderPriceIsGreaterThanTotalPrice() {
            // arrange

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.validateUseCoupon(user, userCoupon, 100L));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                    () -> assertEquals("주문 금액이 쿠폰 사용 최소 비용보다 적습니다.", exception.getCustomMessage())
            );
        }
    }

    @DisplayName("쿠폰 가격을 계산할 때")
    @Nested
    class CalculateUseCouponPrice {

        private UserEntity user;

        @BeforeEach
        void setup() {
            user = Instancio.create(UserEntity.class);
        }

        @DisplayName("주문 금액이 쿠폰 사용 최소 금액보다 작으면 409 에러가 발생한다.")
        @Test
        void throws409Exception_whenInvalidPrice() {
            // arrange
            CouponEntity coupon = Instancio.of(CouponEntity.class)
                    .set(field(CouponEntity::getMinOrderPrice), 3000L)
                    .create();
            ZonedDateTime expired = ZonedDateTime.now().plusDays(12L);
            UserCouponEntity userCoupon = new UserCouponEntity(user, coupon, expired, null, null);

            // act
            CoreException exception = assertThrows(CoreException.class, () -> userCouponDomainService.calculateUseCouponPrice(100L, userCoupon));

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.CONFLICT, exception.getErrorType()),
                    () -> assertEquals("주문 금액이 쿠폰 사용 최소 비용보다 적습니다.", exception.getCustomMessage())
            );
        }

        @DisplayName("정률 쿠폰일 때 계산이 잘 된다.")
        @Test
        void success_whenRateCoupon() {
            // arrange
            CouponEntity coupon = new CouponEntity("쿠폰명", CouponType.RATE, 1000L, 1000L, 10.0, ZonedDateTime.now());
            UserCouponEntity userCoupon = new UserCouponEntity(user, coupon, ZonedDateTime.now().plusDays(2L), null, null);

            // act
            Long result = userCouponDomainService.calculateUseCouponPrice(10000L, userCoupon);

            // assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(9000L, result)
            );
        }

        @DisplayName("정액 쿠폰이 잘 생성이 된다.")
        @Test
        void success_flat_coupon() {
            // arrange
            CouponEntity coupon = new CouponEntity("쿠폰명", CouponType.FLAT, 1000L, 200L, null, ZonedDateTime.now());
            UserCouponEntity userCoupon = new UserCouponEntity(user, coupon, ZonedDateTime.now().plusDays(2L), null, null);

            // act
            Long result = userCouponDomainService.calculateUseCouponPrice(2000L, userCoupon);

            // assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1800L, result)
            );
        }

        @DisplayName("쿠폰이 없으면, 현재 금애 그대로 반환된다.")
        @Test
        void success_whenUserCouponIsNull() {
            // arrange

            // act
            Long result = userCouponDomainService.calculateUseCouponPrice(1000L, null);

            // assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1000L, result)
            );
        }

    }
}
