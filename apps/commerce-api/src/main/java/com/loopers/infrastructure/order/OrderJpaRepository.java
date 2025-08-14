package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.Optional;


public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity save(OrderEntity order);

    @EntityGraph(attributePaths = {"user", "items", "items.product", "items.product.brand", "items.product.productCount"})
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product", "items.product.productCount"})
    @Query("SELECT orders FROM OrderEntity orders WHERE orders.user = :user AND orders.createdAt >= :startDate AND orders.createdAt < :endDate ORDER BY orders.createdAt DESC")
    Page<OrderEntity> findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(@Param("user") UserEntity user, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate, Pageable pageable);
}
