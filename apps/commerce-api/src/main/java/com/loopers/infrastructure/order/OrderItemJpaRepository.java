package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    OrderItemEntity save(OrderItemEntity orderItem);
}
