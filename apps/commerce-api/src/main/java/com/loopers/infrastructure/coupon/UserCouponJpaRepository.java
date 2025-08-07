package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCouponEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {


    @EntityGraph(attributePaths = {"user", "coupon"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCouponEntity uc WHERE uc.id = :id")
    Optional<UserCouponEntity> findByIdWithLock(@Param("id") Long id);

    @EntityGraph(attributePaths = {"user", "coupon"})
    Optional<UserCouponEntity> findById(@Param("id") Long id);
}
