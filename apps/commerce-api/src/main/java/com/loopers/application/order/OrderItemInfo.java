package com.loopers.application.order;

import com.loopers.domain.order.OrderItemEntity;

public record OrderItemInfo(Long id, Long productId, String name, Long price, Long quantity) {
    public static OrderItemInfo from(OrderItemEntity orderItemEntity) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new OrderItemInfo(orderItemEntity.getId(), orderItemEntity.getProduct().getId(), orderItemEntity.getProduct().getName(), orderItemEntity.getPrice(), orderItemEntity.getQuantity());
    }

}
