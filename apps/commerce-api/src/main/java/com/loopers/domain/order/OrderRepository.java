package com.loopers.domain.order;

import com.loopers.domain.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface OrderRepository {
    OrderEntity save(OrderEntity user);

    Page<OrderEntity> findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(UserEntity user, ZonedDateTime startDate, ZonedDateTime endDate, Pageable pageable);

    Optional<OrderEntity> findById(Long orderId);
}
