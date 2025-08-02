package com.loopers.application.order;

import com.loopers.application.product.ProductInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderItemEntity;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderInfo(Long id, UserInfo userInfo, Long totalPrice, List<ProductInfo> items, ZonedDateTime createdAt) {
    public static OrderInfo from(OrderEntity orderEntity) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new OrderInfo(orderEntity.getId(), UserInfo.from(orderEntity.getUser()), orderEntity.getTotalPrice(), orderEntity.getItems().stream().map(OrderItemEntity::getProduct).map(ProductInfo::from).toList(), orderEntity.getCreatedAt());
    }

}
