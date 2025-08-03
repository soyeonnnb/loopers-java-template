package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity save(OrderEntity order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Page<OrderEntity> findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(UserEntity user, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable) {
        return orderJpaRepository.findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(user, startDate, endDate, pageable);
    }

    @Override
    public Optional<OrderEntity> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
