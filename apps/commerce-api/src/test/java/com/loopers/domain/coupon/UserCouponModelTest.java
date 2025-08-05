package com.loopers.domain.coupon;

import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserCouponModelTest {
    @DisplayName("UserCoupon 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("사용자가 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenUserIsNull() {
            // arrange
            UserEntity user = null;
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = false;
            ZonedDateTime usedAt = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("소유자는 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("쿠폰이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenCouponIsNull() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = null;
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = false;
            ZonedDateTime usedAt = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new UserCouponEntity(user, coupon, expiredAt, isUsed, null)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("쿠폰은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("만료일이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenExpiredAtIsNull() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            ZonedDateTime expiredAt = null;
            Boolean isUsed = false;
            ZonedDateTime usedAt = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new UserCouponEntity(user, coupon, expiredAt, isUsed, null)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("만료일은 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("사용여부가 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenIsUsedIsNull() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = null;
            ZonedDateTime usedAt = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("사용여부는 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("사용여부가 true이고, 사용일자가 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenIsUsedIsTrueAndUsedAtIsNull() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = true;
            ZonedDateTime usedAt = null;

            // act
            CoreException coreException = assertThrows(CoreException.class, () ->
                    new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt)
            );

            // assert
            assertAll(
                    () -> assertEquals(GlobalErrorType.BAD_REQUEST, coreException.getErrorType()),
                    () -> assertEquals("사용여부는 null일 수 없습니다.", coreException.getCustomMessage())
            );
        }

        @DisplayName("쿠폰을 사용하지 않았을 때, 모든 필드가 잘 작성되면 객체가 생성이 된다.")
        @Test
        void success_whenNotUsed() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = false;
            ZonedDateTime usedAt = null;

            // act
            UserCouponEntity userCouponEntity = new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt);

            // assert
            assertAll(
                    () -> assertNotNull(userCouponEntity),
                    () -> assertNotNull(userCouponEntity.getUser()),
                    () -> assertEquals(user.getId(), userCouponEntity.getUser().getId()),
                    () -> assertNotNull(userCouponEntity.getCoupon()),
                    () -> assertEquals(coupon.getId(), userCouponEntity.getCoupon().getId()),
                    () -> assertEquals(expiredAt, userCouponEntity.getExpiredAt()),
                    () -> assertEquals(isUsed, userCouponEntity.getIsUsed()),
                    () -> assertEquals(usedAt, userCouponEntity.getUsedAt()),
                    () -> assertEquals(usedAt, userCouponEntity.getUsedAt())
            );
        }

        @DisplayName("쿠폰을 사용하지 않았을 때, usedAt이 null이 아니어도 Null로 객체가 생성이 된다.")
        @Test
        void success_whenNotUsedAndUsedAtIsNotNull() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = false;
            ZonedDateTime usedAt = ZonedDateTime.now();

            // act
            UserCouponEntity userCouponEntity = new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt);

            // assert
            assertAll(
                    () -> assertNotNull(userCouponEntity),
                    () -> assertNotNull(userCouponEntity.getUser()),
                    () -> assertEquals(user.getId(), userCouponEntity.getUser().getId()),
                    () -> assertNotNull(userCouponEntity.getCoupon()),
                    () -> assertEquals(coupon.getId(), userCouponEntity.getCoupon().getId()),
                    () -> assertEquals(expiredAt, userCouponEntity.getExpiredAt()),
                    () -> assertEquals(isUsed, userCouponEntity.getIsUsed()),
                    () -> assertNull(userCouponEntity.getUsedAt())
            );
        }

        @DisplayName("쿠폰을 사용하지 않았을 때, 모든 필드가 잘 작성되면 객체가 생성이 된다.")
        @Test
        void success_whenUsed() {
            // arrange
            UserEntity user = Instancio.create(UserEntity.class);
            CouponEntity coupon = Instancio.create(CouponEntity.class);
            LocalDateTime localDateTime = LocalDateTime.of(0, 1, 1, 10, 30);
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime expiredAt = ZonedDateTime.of(localDateTime, zoneId);
            Boolean isUsed = true;
            ZonedDateTime usedAt = ZonedDateTime.now();

            // act
            UserCouponEntity userCouponEntity = new UserCouponEntity(user, coupon, expiredAt, isUsed, usedAt);

            // assert
            assertAll(
                    () -> assertNotNull(userCouponEntity),
                    () -> assertNotNull(userCouponEntity.getUser()),
                    () -> assertEquals(user.getId(), userCouponEntity.getUser().getId()),
                    () -> assertNotNull(userCouponEntity.getCoupon()),
                    () -> assertEquals(coupon.getId(), userCouponEntity.getCoupon().getId()),
                    () -> assertEquals(expiredAt, userCouponEntity.getExpiredAt()),
                    () -> assertEquals(isUsed, userCouponEntity.getIsUsed()),
                    () -> assertEquals(usedAt, userCouponEntity.getUsedAt())
            );
        }
    }
}
