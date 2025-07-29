package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like/products")
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @Override
    @PostMapping("/{productId}")
    public ApiResponse<LikeV1Dto.LikeResponse> like(@RequestHeader("X-USER-ID") String userId, Long productId) {
        LikeInfo likeInfo = likeFacade.like(userId, productId);
        return ApiResponse.success(LikeV1Dto.LikeResponse.from(likeInfo));
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<LikeV1Dto.LikeResponse> dislike(String userId, Long productId) {
        LikeInfo likeInfo = likeFacade.dislike(userId, productId);
        return ApiResponse.success(LikeV1Dto.LikeResponse.from(likeInfo));
    }
}
