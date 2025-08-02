package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderItemEntity;
import com.loopers.domain.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItemEntity save(OrderItemEntity orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }
}
