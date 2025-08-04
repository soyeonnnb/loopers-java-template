package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Table(name = "coupons")
public class CouponEntity extends BaseEntity {

    @Schema(name = "쿠폰명")
    @Column(nullable = false)
    private String name;

    @Schema(name = "쿠폰 타입")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Schema(name = "최소 주문 금액")
    @Column(nullable = false)
    private Long minOrderPrice;

    @Schema(name = "최대 사용 금액")
    @Column(nullable = false)
    private Long maxUsePrice;

    @Schema(name = "할인률")
    private Double rate;

    @Schema(name = "유효기간")
    @Column(nullable = false)
    private ZonedDateTime expiryDate;

    protected CouponEntity() {

    }

    public CouponEntity(String name, String type, Long minOrderPrice, Long maxUsePrice, Double rate, ZonedDateTime expiryDate) {
        if (name == null || name.isBlank()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰명은 null 이거나 비어있을 수 없습니다.");
        }

        if (type == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰 타입은 null일 수 없습니다.");
        }

        if (minOrderPrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최소 주문 금액은 null일 수 없습니다.");
        } else if (minOrderPrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최소 주문 금액은 0원 이상이여야 합니다.");
        }

        if (maxUsePrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최대 사용 금액은 null일 수 없습니다.");
        } else if (maxUsePrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최대 사용 금액은 0원 이상이여야 합니다.");
        }

        if (type.equals(CouponType.RATE.toString())) {
            if (rate == null) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "정률 쿠폰의 rate는 null일 수 없습니다.");
            } else if (rate < 0) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "할인률은 0% 이상이여야 합니다.");
            }
        }

        if (expiryDate == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰의 유효기간은 null일 수 없습니다.");
        }

        if (type.equals(CouponType.FLAT.toString()) && minOrderPrice < maxUsePrice) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "정액 쿠폰의 경우, 최소 주문 금액이 쿠폰 금액 이상이여야 합니다.");
        }

        this.name = name;
        this.type = CouponType.from(type);
        this.minOrderPrice = minOrderPrice;
        this.maxUsePrice = maxUsePrice;
        this.rate = type.equals(CouponType.RATE.toString()) ? rate : null;
        this.expiryDate = expiryDate;

    }

    public CouponEntity(String name, CouponType type, Long minOrderPrice, Long maxUsePrice, Double rate, ZonedDateTime expiryDate) {
        if (name == null || name.isBlank()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰명은 null 이거나 비어있을 수 없습니다.");
        }

        if (type == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰 타입은 null일 수 없습니다.");
        }

        if (minOrderPrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최소 주문 금액은 null일 수 없습니다.");
        } else if (minOrderPrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최소 주문 금액은 0원 이상이여야 합니다.");
        }

        if (maxUsePrice == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최대 사용 금액은 null일 수 없습니다.");
        } else if (maxUsePrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "최대 사용 금액은 0원 이상이여야 합니다.");
        }

        if (type.equals(CouponType.RATE)) {
            if (rate == null) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "정률 쿠폰의 rate는 null일 수 없습니다.");
            } else if (rate < 0) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "할인률은 0% 이상이여야 합니다.");
            }
        }

        if (expiryDate == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "쿠폰의 유효기간은 null일 수 없습니다.");
        }

        if (type.equals(CouponType.FLAT) && minOrderPrice < maxUsePrice) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "정액 쿠폰의 경우, 최소 주문 금액이 쿠폰 금액 이상이여야 합니다.");
        }

        this.name = name;
        this.type = type;
        this.minOrderPrice = minOrderPrice;
        this.maxUsePrice = maxUsePrice;
        this.rate = type.equals(CouponType.RATE) ? rate : null;
        this.expiryDate = expiryDate;

    }
}
