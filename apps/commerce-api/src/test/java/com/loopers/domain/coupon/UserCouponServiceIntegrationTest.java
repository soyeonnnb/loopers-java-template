package com.loopers.domain.coupon;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserCouponServiceIntegrationTest {
    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("사용자 쿠폰을 조회할 때,")
    @Nested
    class GetUserCouponInfo {
        @DisplayName("정보가 존재하면, 사용자 쿠폰의 정보가 반환된다.")
        @Test
        void returnUserCouponInfo_whenValidCouponId() {
            // arrange
            UserEntity user = userRepository.save(Instancio.of(UserEntity.class)
                    .set(field(UserEntity::getId), null)
                    .create());

            ZonedDateTime expiredAt = ZonedDateTime.now().plusDays(12);
            CouponEntity coupon = couponRepository.save(new CouponEntity("정액 쿠폰", "FLAT", 3000L, 200L, null, expiredAt));
            UserCouponEntity userCoupon = userCouponRepository.save(new UserCouponEntity(user, coupon, expiredAt, null, null));

            // act
            Optional<UserCouponEntity> result = userCouponService.getCouponInfo(userCoupon.getId());

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assertTrue(result.isPresent());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getId(), result.get().getCoupon().getId());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getName(), result.get().getCoupon().getName());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getRate(), result.get().getCoupon().getRate());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getType(), result.get().getCoupon().getType());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getMinOrderPrice(), result.get().getCoupon().getMinOrderPrice());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(coupon.getMaxUsePrice(), result.get().getCoupon().getMaxUsePrice());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(userCoupon.getId(), result.get().getId());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(user.getId(), result.get().getUser().getId());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(userCoupon.getUsedAt(), result.get().getUsedAt());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertEquals(userCoupon.getBeforePrice(), result.get().getBeforePrice());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertTrue(userCoupon.getExpiredAt().isEqual(result.get().getExpiredAt()));
                    }
            );
        }

        @DisplayName("정보가 존재하지 않으면, null이 반환된다.")
        @Test
        void returnNull_whenInvalidBrandId() {
            // arrange

            // act
            Optional<UserCouponEntity> optionalUserCouponEntity = userCouponService.getCouponInfo(1L);

            // assert
            assertAll(
                    () -> assertTrue(optionalUserCouponEntity.isEmpty())
            );
        }
    }

}
