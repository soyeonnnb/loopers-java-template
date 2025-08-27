package com.loopers.interfaces.listener.coupon;

import com.loopers.domain.coupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponUseEventListener {

    private final UserCouponService userCouponService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleUseCoupon(UserCouponUseEvent event) {
        log.info("handleUseCoupon 발생");
        userCouponService.useCoupon(event.getPaymentId(), event.getUserCouponId(), event.getUseBeforePrice());
    }

}
