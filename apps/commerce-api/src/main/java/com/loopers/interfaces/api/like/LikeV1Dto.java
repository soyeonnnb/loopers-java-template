package com.loopers.interfaces.api.like;


import com.loopers.application.like.LikeInfo;

public class LikeV1Dto {
    public record LikeResponse(Long id, Long userId, String loginId, Long productId, String productName, Boolean isLike) {
        public LikeResponse(Long id, Long userId, String loginId, Long productId, String productName, Boolean isLike) {
            this.id = id;
            this.userId = userId;
            this.loginId = loginId;
            this.productId = productId;
            this.productName = productName;
            this.isLike = isLike;
        }

        public static LikeV1Dto.LikeResponse from(LikeInfo likeInfo) {
            return new LikeV1Dto.LikeResponse(likeInfo.id(), likeInfo.userInfo().id(), likeInfo.userInfo().loginId(), likeInfo.productInfo().id(), likeInfo.productInfo().name(), likeInfo.isLike());
        }
    }
}
