package com.loopers.domain.coupon;

import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCouponEntity> findByIdWithLock(Long id);

    UserCouponEntity save(UserCouponEntity userCouponEntity);

    Optional<UserCouponEntity> findById(Long couponId);
}
