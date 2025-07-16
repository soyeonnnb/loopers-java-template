package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.RequireUserId;
import com.loopers.interfaces.api.user.UserV1Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Point V1 API", description = "Point API")
public interface PointV1ApiSpec {


    @Operation(
            summary = "보유 포인트 조회",
            description = "보유 포인트를 조회합니다."
    )
    @RequireUserId
    ApiResponse<PointV1Dto.PointResponse> getPoint(
            @RequestHeader("X-USER-ID") String userId
    );

}
