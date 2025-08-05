package com.loopers.domain.coupon;

import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCouponEntity> findById(Long id);

    UserCouponEntity save(UserCouponEntity userCouponEntity);
}
