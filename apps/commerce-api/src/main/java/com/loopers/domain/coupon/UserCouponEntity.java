package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Table(name = "user_coupon")
public class UserCouponEntity extends BaseEntity {

    @Schema(name = "소유자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Schema(name = "쿠폰")
    @ManyToOne(fetch = FetchType.LAZY)
    private CouponEntity coupon;

    @Schema(name = "만료일자")
    @Column(nullable = false)
    private ZonedDateTime expiredAt;

    @Schema(name = "사용일자")
    private ZonedDateTime usedAt;

    @Schema(name = "쿠폰 적용 전 가격")
    private Long beforePrice;

    protected UserCouponEntity() {

    }

    public UserCouponEntity(UserEntity user, CouponEntity coupon, ZonedDateTime expiredAt, ZonedDateTime usedAt, Long beforePrice) {
        if (user == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "소유자는 null일 수 없습니다.");
        }

        if (coupon == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰은 null일 수 없습니다.");
        }

        if (expiredAt == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "만료일은 null일 수 없습니다.");
        }
        if ((usedAt != null && beforePrice == null) || (usedAt == null && beforePrice != null)) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "사용시간과 사용전 금액의 존재 여부는 일치해야 합니다.");
        }

        this.user = user;
        this.coupon = coupon;
        this.expiredAt = expiredAt;
        this.usedAt = usedAt;
        this.beforePrice = beforePrice;

    }

    public void use(Long beforePrice) {
        this.usedAt = ZonedDateTime.now();
        this.beforePrice = beforePrice;
    }
}
