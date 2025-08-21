package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
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

    @Override
    public Optional<OrderEntity> findByUuidAndPaymentTransactionKeyWithLock(String orderUUID, String transactionKey) {
        return orderJpaRepository.findByUuidAndPaymentTransactionKeyWithLock(orderUUID, transactionKey);
    }

    @Override
    public List<OrderEntity> findOrdersByStatusAndCreatedAtBetweenWithCardPay(OrderStatus orderStatus, ZonedDateTime before2Minutes, ZonedDateTime before1Minutes) {
        return orderJpaRepository.findOrdersByStatusAndCreatedAtBetweenWithCardPay(orderStatus, before2Minutes, before1Minutes);
    }
}
