package com.loopers.application.like;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LikeFacade {
    private final LikeService likeService;
    private final UserService userService;
    private final ProductService productService;

    public LikeInfo like(String userId, Long productId) {
        if (userId == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 ID 정보가 없습니다.");
        }

        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }

        Optional<ProductEntity> optionalProductEntity = productService.getProductInfo(productId);
        if (optionalProductEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다.");
        }

        LikeEntity likeEntity = likeService.like(optionalUserEntity.get(), optionalProductEntity.get());
        return LikeInfo.from(likeEntity);
    }


    public LikeInfo dislike(String userId, Long productId) {
        if (userId == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 ID 정보가 없습니다.");
        }

        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }

        Optional<ProductEntity> optionalProductEntity = productService.getProductInfo(productId);
        if (optionalProductEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다.");
        }

        LikeEntity likeEntity = likeService.dislike(optionalUserEntity.get(), optionalProductEntity.get());
        return LikeInfo.from(likeEntity);
    }
}
