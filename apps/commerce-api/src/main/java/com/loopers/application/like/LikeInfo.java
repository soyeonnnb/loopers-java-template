package com.loopers.application.like;

import com.loopers.application.product.ProductInfo;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.like.LikeEntity;

public record LikeInfo(Long id, UserInfo userInfo, ProductInfo productInfo, Boolean isLike) {
    public static LikeInfo from(LikeEntity like) {
        // 일단 좋아요는 false 처리 -> 기능 구현 후 수정
        return new LikeInfo(like.getId(), UserInfo.from(like.getUser()), ProductInfo.from(like.getProduct()), like.getIsLike());
    }
}
