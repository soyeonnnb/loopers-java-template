package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCouponEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserCouponJpaRepository extends JpaRepository<UserCouponEntity, Long> {


    @EntityGraph(attributePaths = {"user", "coupon"})
    Optional<UserCouponEntity> findById(Long id);
}
