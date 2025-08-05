package com.loopers.domain.coupon;

import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserCouponDomainService {

    public void validateUseCoupon(UserEntity user, UserCouponEntity userCoupon, Long totalPrice) {
        // 0. 파라미터 확인
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "쿠폰 사용 시, 사용자가 필수입니다.");
        }
        if (userCoupon == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "검증할 쿠폰이 없습니다.");
        }
        if (totalPrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "검증할 금액이 없습니다.");
        }
        // 1. 사용자 검증
        if (userCoupon.getUser().getId() != user.getId()) {
            throw new CoreException(GlobalErrorType.FORBIDDEN, "로그인한 사용자의 쿠폰이 아닙니다.");
        }
        // 2. 사용여부 확인
        if (userCoupon.getIsUsed()) {
            throw new CoreException(GlobalErrorType.CONFLICT, "이미 사용한 쿠폰입니다.");
        }
        // 3. 사용기간 확인
        ZonedDateTime now = ZonedDateTime.now();
        if (userCoupon.getExpiredAt().isBefore(now) || userCoupon.getExpiredAt().equals(now)) {
            throw new CoreException(GlobalErrorType.CONFLICT, "쿠폰 유효기간이 지났습니다.");
        }
        // 4. 가격 확인
        if (userCoupon.getCoupon().getMinOrderPrice() > totalPrice) {
            throw new CoreException(GlobalErrorType.CONFLICT, "주문 금액이 쿠폰 사용 최소 비용보다 적습니다.");
        }
    }

    public Long calculateUseCouponPrice(Long totalPrice, UserCouponEntity userCoupon) {
        if (userCoupon == null) {
            return totalPrice;
        }
        if (userCoupon.getCoupon().getMinOrderPrice() > totalPrice) {
            throw new CoreException(GlobalErrorType.CONFLICT, "주문 금액이 쿠폰 사용 최소 비용보다 적습니다.");
        }

        switch (userCoupon.getCoupon().getType()) {
            case FLAT -> {
                return totalPrice - userCoupon.getCoupon().getMaxUsePrice();
            }
            case RATE -> {
                BigDecimal price = BigDecimal.valueOf(totalPrice);
                BigDecimal rate = BigDecimal.valueOf(userCoupon.getCoupon().getRate());
                BigDecimal discount = price.multiply(rate).divide(BigDecimal.valueOf(100));
                BigDecimal salePrice = discount.min(BigDecimal.valueOf(userCoupon.getCoupon().getMaxUsePrice()));
                return price.subtract(salePrice).longValue();
            }
            default -> {
                return totalPrice;
            }
        }
    }
}
