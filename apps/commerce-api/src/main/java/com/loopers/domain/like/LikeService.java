package com.loopers.domain.like;

import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public LikeEntity like(UserEntity userEntity, ProductEntity productEntity) {
        if (userEntity == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다.");
        }
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserAndProduct(userEntity, productEntity);
        LikeEntity likeEntity = optionalLikeEntity.orElse(new LikeEntity(userEntity, productEntity, true));

        likeEntity.like();
        return likeRepository.save(likeEntity);
    }
}
