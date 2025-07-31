package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity save(OrderEntity order);
}
