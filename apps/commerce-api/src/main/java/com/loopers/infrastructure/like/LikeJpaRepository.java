package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findById(Long id);

    Optional<LikeEntity> findByUserAndProduct(UserEntity userEntity, ProductEntity productEntity);
}
