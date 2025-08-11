package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponModelTest {
    @DisplayName("Coupon 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("쿠폰명이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsNull() {
            // arrange
            String name = null;
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰명은 null 이거나 비어있을 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("쿠폰명이 비어있으면 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "    "
        })
        void throwsBadRequestException_whenNameIsEmpty(String name) {
            // arrange
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰명은 null 이거나 비어있을 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("쿠폰 타입이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenTypeIsNull() {
            // arrange
            String name = "쿠폰명";
            CouponType type = null;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰 타입은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("최소 주문 금액이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenMinOrderPriceIsNull() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = null;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("최소 주문 금액은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("최소 주문 금액이 음수면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenMinOrderPriceIsNegativeNumber() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = -1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("최소 주문 금액은 0원 이상이여야 합니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("최대 사용 금액이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenMaxUsePriceIsNull() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = null;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("최대 사용 금액은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("최대 사용 금액이 음수면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenMaxUsePriceIsNegativeNumber() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = -200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("최대 사용 금액은 0원 이상이여야 합니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("타입이 rate인데, 할인률이 Null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenTypeIsRateAndRateIsNull() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = null;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("정률 쿠폰의 rate는 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("타입이 rate인데, 할인률이 음수면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenTypeIsRateAndRateIsNegativeNumber() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = -2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("할인률은 0% 이상이여야 합니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("타입이 flat이면, 할인률은 항상 null이 된다.")
        @Test
        void rateIsNull_whenTypeIsFlat() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.FLAT;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CouponEntity coupon = new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate);

            // assert
            assertAll(
                    () -> assertNotNull(coupon),
                    () -> assertEquals(CouponType.FLAT, coupon.getType()),
                    () -> assertNull(coupon.getRate())
            );
        }

        @DisplayName("타입이 String일 때, FLAT, RATE외에는 항상 실패한다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "flat",
                "Flat",
                "rate",
                "Rate",
                "정액쿠폰",
                "정률쿠폰",
        })
        void throwsBadRequestException_whenTypeIsInvalid(String type) {
            // arrange
            String name = "쿠폰명";
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰 타입이 유효하지 않습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("유효기간이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenExpiryDateIsNull() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            ZonedDateTime expiredDate = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰의 유효기간은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("정액 쿠폰의 경우, 최소 주문 금액이 쿠폰 금액 이상이여야 합니다.")
        @Test
        void throwsBadRequestException_whenQuantityIsNegativeNumber() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.FLAT;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 20000L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("정액 쿠폰의 경우, 최소 주문 금액이 쿠폰 금액 이상이여야 합니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("정액 쿠폰이 잘 생성이 된다.")
        @Test
        void success_flat_coupon() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.FLAT;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = null;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CouponEntity coupon = new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate);

            // assert
            assertAll(
                    () -> assertNotNull(coupon),
                    () -> assertEquals(name, coupon.getName()),
                    () -> assertEquals(type, coupon.getType()),
                    () -> assertEquals(minOrderPrice, coupon.getMinOrderPrice()),
                    () -> assertEquals(maxUsePrice, coupon.getMaxUsePrice()),
                    () -> assertEquals(rate, coupon.getRate()),
                    () -> assertEquals(expiredDate, coupon.getExpiryDate())
            );
        }

        @DisplayName("정률 쿠폰이 잘 생성이 된다.")
        @Test
        void success_rate_coupon() {
            // arrange
            String name = "쿠폰명";
            CouponType type = CouponType.RATE;
            Long minOrderPrice = 1000L;
            Long maxUsePrice = 200L;
            Double rate = 2.0;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredDate = ZonedDateTime.of(localDateTime, zoneId);

            // act
            CouponEntity coupon = new CouponEntity(name, type, minOrderPrice, maxUsePrice, rate, expiredDate);

            // assert
            assertAll(
                    () -> assertNotNull(coupon),
                    () -> assertEquals(name, coupon.getName()),
                    () -> assertEquals(type, coupon.getType()),
                    () -> assertEquals(minOrderPrice, coupon.getMinOrderPrice()),
                    () -> assertEquals(maxUsePrice, coupon.getMaxUsePrice()),
                    () -> assertEquals(rate, coupon.getRate()),
                    () -> assertEquals(expiredDate, coupon.getExpiryDate())
            );
        }
    }
}
