package com.loopers.application.product;

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

    public ProductInfo getProductInfo(String userId, Long productId) {
        if (productId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 ID가 존재하지 않습니다.");
        }
        Optional<ProductEntity> optionalProductEntity = productService.getProductInfo(productId);
        if (optionalProductEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 ID에 해당하는 데이터가 없습니다.");
        }
        // ToDo: 좋아요 기능 연결
        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);
        Boolean isLike = false;
        if (optionalUserEntity.isPresent()) {
            isLike = true;
        }
        Long totalLike = 1L;

        return ProductInfo.from(optionalProductEntity.get(), isLike, totalLike);
    }


}
