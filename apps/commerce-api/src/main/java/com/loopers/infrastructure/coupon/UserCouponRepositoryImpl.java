package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.coupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public Optional<UserCouponEntity> findByIdWithLock(Long id) {
        return userCouponJpaRepository.findByIdWithLock(id);
    }

    @Override
    public UserCouponEntity save(UserCouponEntity userCouponEntity) {
        return userCouponJpaRepository.save(userCouponEntity);
    }

    @Override
    public Optional<UserCouponEntity> findById(Long couponId) {
        return userCouponJpaRepository.findById(couponId);
    }
}
