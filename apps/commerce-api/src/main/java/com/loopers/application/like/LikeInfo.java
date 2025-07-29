package com.loopers.application.like;

import com.loopers.application.product.ProductInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;

public record LikeInfo(Long id, UserInfo userInfo, ProductInfo productInfo, Boolean isLike) {
    public static LikeInfo from(LikeEntity like, UserEntity user, ProductEntity product) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new LikeInfo(like.getId(), UserInfo.from(user), ProductInfo.from(product), like.getIsLike());
    }
}
