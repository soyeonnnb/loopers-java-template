package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderInfo;
import com.loopers.interfaces.api.coupon.UserCouponV1Dto;
import com.loopers.interfaces.api.product.ProductV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderV1Dto {
    public record OrderRequest(@NotNull List<ProductOrderRequest> items, @NotNull Long totalPrice, Long couponId) {

    }

    public record ProductOrderRequest(@NotNull Long id, @NotNull Long quantity) {

    }


    public record OrderResponse(Long id, UserV1Dto.UserResponse userInfo, Long totalPrice,
                                List<ProductV1Dto.ProductResponse> items, ZonedDateTime createdAt,
                                UserCouponV1Dto.UserCouponResponse userCouponInfo) {
        public static OrderResponse from(OrderInfo orderInfo) {
            return new OrderResponse(orderInfo.id(),
                    UserV1Dto.UserResponse.from(orderInfo.userInfo()),
                    orderInfo.totalPrice(),
                    orderInfo.items().stream().map(ProductV1Dto.ProductResponse::from).toList(),
                    orderInfo.createdAt(),
                    UserCouponV1Dto.UserCouponResponse.from(orderInfo.userCouponInfo()))
                    ;
        }
    }
}
