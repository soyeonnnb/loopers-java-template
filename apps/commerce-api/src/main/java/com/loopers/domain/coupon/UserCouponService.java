package com.loopers.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public Optional<UserCouponEntity> getCouponInfo(Long couponId) {
        return userCouponRepository.findById(couponId);
    }
}
