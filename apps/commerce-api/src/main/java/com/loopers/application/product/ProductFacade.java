package com.loopers.application.product;

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
public class ProductFacade {
    private final UserService userService;
    private final ProductService productService;
    private final LikeService likeService;

    public ProductInfo getProductInfo(String userId, Long productId) {
        if (productId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 ID가 존재하지 않습니다.");
        }
        Optional<ProductEntity> optionalProductEntity = productService.getProductInfo(productId);
        if (optionalProductEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 ID에 해당하는 데이터가 없습니다.");
        }

        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);

        boolean isLike = false;
        if (optionalUserEntity.isPresent()) {
            Optional<LikeEntity> optionalLikeEntity = likeService.getUserLikeProduct(optionalUserEntity.get(), optionalProductEntity.get());
            isLike = optionalLikeEntity.isPresent() && optionalLikeEntity.get().getIsLike();
        }

        return ProductInfo.from(optionalProductEntity.get(), isLike);
    }


}
