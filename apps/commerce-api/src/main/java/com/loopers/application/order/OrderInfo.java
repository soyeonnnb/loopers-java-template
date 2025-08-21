package com.loopers.application.order;

import com.loopers.application.coupon.UserCouponInfo;
import com.loopers.application.product.ProductInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderItemEntity;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderInfo(Long id, UserInfo userInfo, Long totalPrice, List<ProductInfo> items, ZonedDateTime createdAt,
                        UserCouponInfo userCouponInfo, Boolean payResult) {
    public static OrderInfo from(OrderEntity orderEntity) {
        return new OrderInfo(orderEntity.getId(), UserInfo.from(orderEntity.getUser()), orderEntity.getTotalPrice(), orderEntity.getItems().stream().map(OrderItemEntity::getProduct).map(ProductInfo::from).toList(), orderEntity.getCreatedAt(), UserCouponInfo.from(orderEntity.getUserCoupon()), true);
    }

    public static OrderInfo from(OrderEntity orderEntity, UserCouponEntity userCoupon) {
        return new OrderInfo(orderEntity.getId(), UserInfo.from(orderEntity.getUser()), orderEntity.getTotalPrice(), orderEntity.getItems().stream().map(OrderItemEntity::getProduct).map(ProductInfo::from).toList(), orderEntity.getCreatedAt(), UserCouponInfo.from(userCoupon), true);
    }

    public static OrderInfo from(OrderEntity orderEntity, Boolean payResult) {
        return new OrderInfo(orderEntity.getId(), UserInfo.from(orderEntity.getUser()), orderEntity.getTotalPrice(), orderEntity.getItems().stream().map(OrderItemEntity::getProduct).map(ProductInfo::from).toList(), orderEntity.getCreatedAt(), UserCouponInfo.from(orderEntity.getUserCoupon()), payResult);
    }

    public static OrderInfo from(OrderEntity orderEntity, UserCouponEntity userCoupon, Boolean payResult) {
        return new OrderInfo(orderEntity.getId(), UserInfo.from(orderEntity.getUser()), orderEntity.getTotalPrice(), orderEntity.getItems().stream().map(OrderItemEntity::getProduct).map(ProductInfo::from).toList(), orderEntity.getCreatedAt(), UserCouponInfo.from(userCoupon), payResult);
    }
}
