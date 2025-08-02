package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto;
import com.loopers.interfaces.api.user.RequireUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Like V1 API", description = "Like API")
public interface LikeV1ApiSpec {

    @Operation(
            summary = "상품 좋아요 등록",
            description = "상품을 좋아요 합니다."
    )
    @RequireUserId
    ApiResponse<LikeV1Dto.LikeResponse> like(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable("productId") Long productId);

    @Operation(
            summary = "상품 좋아요 취소",
            description = "상품을 좋아요 취소합니다."
    )
    @RequireUserId
    ApiResponse<LikeV1Dto.LikeResponse> dislike(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable("productId") Long productId);

    @Operation(
            summary = "좋아요 상품 조회",
            description = "사용자가 좋아요한 상품을 조회합니다."
    )
    @RequireUserId
    ApiResponse<List<ProductV1Dto.ProductResponse>> getLikeProducts(@RequestHeader(value = "X-USER-ID", required = false) String userId);

}
