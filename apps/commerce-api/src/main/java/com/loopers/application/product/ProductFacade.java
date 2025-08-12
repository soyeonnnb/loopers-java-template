package com.loopers.application.product;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandService;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductFacade {
    private final UserService userService;
    private final ProductService productService;
    private final LikeService likeService;
    private final BrandService brandService;

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

    public List<ProductInfo> getProductInfoList(String userId, Long brandId, ProductSortOrder order, Integer size, Integer page) {
        Optional<UserEntity> optionalUserEntity = Optional.empty();
        if (userId != null) {
            optionalUserEntity = userService.getUserInfo(userId);
            if (optionalUserEntity.isEmpty()) {
                throw new CoreException(GlobalErrorType.NOT_FOUND, "사용자 정보를 찾을 수 없습니다.");
            }
        }
        Optional<BrandEntity> optionalBrandEntity = Optional.empty();
        if (brandId != null) {
            optionalBrandEntity = brandService.getBrandInfo(brandId);
            if (optionalBrandEntity.isEmpty()) {
                throw new CoreException(GlobalErrorType.NOT_FOUND, "브랜드 정보를 찾을 수 없습니다.");
            }
        }

        Page<ProductEntity> productEntityList = productService.getProductInfoList(optionalBrandEntity, order, size, page);
        List<ProductInfo> productInfoList = new ArrayList<>();
        if (optionalUserEntity.isPresent()) {
            for (ProductEntity productEntity : productEntityList) {
                Optional<LikeEntity> optionalLikeEntity = likeService.getUserLikeProduct(optionalUserEntity.get(), productEntity);
                productInfoList.add(ProductInfo.from(productEntity, optionalLikeEntity.isPresent() && optionalLikeEntity.get().getIsLike()));
            }
        } else {
            productInfoList = productEntityList.stream().map(ProductInfo::from).toList();
        }
        return productInfoList;
    }
}
