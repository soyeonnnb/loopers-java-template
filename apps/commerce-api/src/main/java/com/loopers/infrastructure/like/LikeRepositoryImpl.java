package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public LikeEntity save(LikeEntity likeEntity) {
        return likeJpaRepository.save(likeEntity);
    }

    @Override
    public Optional<LikeEntity> findByUserAndProduct(UserEntity userEntity, ProductEntity productEntity) {
        return likeJpaRepository.findByUserAndProduct(userEntity, productEntity);
    }

    @Override
    public List<LikeEntity> findAllByUserAndIsLikeIsTrue(UserEntity userEntity) {
        return likeJpaRepository.findAllByUserAndIsLikeIsTrue(userEntity);
    }
}
