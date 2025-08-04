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
@Table(name = "user_coupons")
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

    @Schema(name = "사용여부")
    @Column(nullable = false)
    private Boolean isUsed;

    protected UserCouponEntity() {

    }

    public UserCouponEntity(UserEntity user, CouponEntity coupon, ZonedDateTime expiredAt, Boolean isUsed) {
        if (user == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "소유자는 null일 수 없습니다.");
        }

        if (coupon == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰은 null일 수 없습니다.");
        }

        if (expiredAt == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "만료일은 null일 수 없습니다.");
        }

        if (isUsed == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "사용여부는 null일 수 없습니다.");
        }

        this.user = user;
        this.coupon = coupon;
        this.expiredAt = expiredAt;
        this.isUsed = isUsed;


    }
}
