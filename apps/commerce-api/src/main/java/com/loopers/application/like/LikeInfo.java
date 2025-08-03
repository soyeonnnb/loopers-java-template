package com.loopers.application.like;

import com.loopers.application.product.ProductInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;

public record LikeInfo(UserInfo userInfo, ProductInfo productInfo, Boolean isLike) {
    public static LikeInfo from(LikeEntity like, UserEntity user, ProductEntity product) {
        return new LikeInfo(UserInfo.from(user), ProductInfo.from(product), like != null && like.getIsLike());
    }
}
