package com.loopers.interfaces.api.point;

import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.RequireUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final UserFacade userFacade;

    @Override
    @GetMapping
    @RequireUserId
    public ApiResponse<PointV1Dto.PointResponse> getPoint(@RequestHeader("X-USER-ID") String userId) {
        Long userPointInfo = userFacade.getUserPoint(userId);
        return ApiResponse.success(PointV1Dto.PointResponse.from(userPointInfo));
    }

    @Override
    @PostMapping("/charge")
    @RequireUserId
    public ApiResponse<PointV1Dto.PointResponse> chargePoint(@RequestHeader("X-USER-ID") String userId, @RequestBody PointV1Dto.ChargePointRequest request) {
        Long userPointInfo = userFacade.chargeUserPoint(userId, request);
        return ApiResponse.success(PointV1Dto.PointResponse.from(userPointInfo));
    }
}
