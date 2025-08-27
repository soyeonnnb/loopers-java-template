package com.loopers.domain.coupon;

import com.loopers.interfaces.listener.payment.PaymentFailEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Optional<UserCouponEntity> getCouponInfoWithLock(Long couponId) {
        Optional<UserCouponEntity> optionalUserCouponEntity = userCouponRepository.findByIdWithLock(couponId);
        if (optionalUserCouponEntity.isPresent() && optionalUserCouponEntity.get().getDeletedAt() != null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "쿠폰이 이미 삭제되었습니다.");
        }
        if (optionalUserCouponEntity.isPresent() && optionalUserCouponEntity.get().getUsedAt() != null) {
            throw new CoreException(GlobalErrorType.CONFLICT, "쿠폰이 이미 사용되었습니다.");
        }

        return optionalUserCouponEntity;
    }

    @Transactional
    public Optional<UserCouponEntity> getCouponInfo(Long couponId) {
        Optional<UserCouponEntity> optionalUserCouponEntity = userCouponRepository.findById(couponId);
        if (optionalUserCouponEntity.isPresent() && optionalUserCouponEntity.get().getDeletedAt() != null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "쿠폰이 이미 삭제되었습니다.");
        }
        return optionalUserCouponEntity;
    }

    @Transactional
    public void useCoupon(Long paymentId, Long couponId, Long calculatePrice) {
        UserCouponEntity userCoupon = userCouponRepository.findById(couponId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        try {
            userCoupon.use(calculatePrice);
        } catch (CoreException e) {
            eventPublisher.publishEvent(new PaymentFailEvent(paymentId, "이미 사용된 쿠폰입니다."));
        }
    }
}
