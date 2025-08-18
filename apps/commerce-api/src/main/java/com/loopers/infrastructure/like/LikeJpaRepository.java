package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    @EntityGraph(attributePaths = {"user", "product"})
    Optional<LikeEntity> findById(Long id);

    @EntityGraph(attributePaths = {"user", "product"})
    Optional<LikeEntity> findByUserAndProduct(UserEntity userEntity, ProductEntity productEntity);

    @EntityGraph(attributePaths = {"product"})
    List<LikeEntity> findAllByUserAndIsLikeIsTrue(UserEntity userEntity);

    @EntityGraph(attributePaths = {"user", "product"})
    @Query("SELECT l FROM LikeEntity l WHERE l.user.id = :userId AND l.product.id = :productId")
    Optional<LikeEntity> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
