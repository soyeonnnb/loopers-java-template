package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.RequireUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Like V1 API", description = "Like API")
public interface LikeV1ApiSpec {

    @Operation(
            summary = "상품 좋아요 등록",
            description = "상품을 좋아요 합니다."
    )
    @RequireUserId
    ApiResponse<LikeV1Dto.LikeResponse> getLikeInfo(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable("productId") Long productId);
}
