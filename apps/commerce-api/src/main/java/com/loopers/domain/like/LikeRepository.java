package com.loopers.domain.like;

import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    LikeEntity save(LikeEntity likeEntity);

    Optional<LikeEntity> findByUserAndProduct(UserEntity userEntity, ProductEntity productEntity);

    Optional<LikeEntity> findByUserIdAndProductId(Long userId, Long productId);

    List<LikeEntity> findAllByUserAndIsLikeIsTrue(UserEntity userEntity);
}
