package com.loopers.interfaces.api.product;

import com.loopers.application.product.BrandFacade;
import com.loopers.application.product.BrandInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandV1Controller implements BrandV1ApiSpec {

    private final BrandFacade brandFacade;
//    @Override
//    @PostMapping("/charge")
//    @RequireUserId
//    public ApiResponse<BrandV1Dto.PointResponse> chargePoint(@RequestHeader("X-USER-ID") String userId, @RequestBody BrandV1Dto.ChargePointRequest request) {
//        Long userPointInfo = userFacade.chargeUserPoint(userId, request);
//        return ApiResponse.success(BrandV1Dto.PointResponse.from(userPointInfo));
//    }

    @Override
    @GetMapping("/{brandId}")
    public ApiResponse<BrandV1Dto.BrandResponse> getBrandInfo(@PathVariable("brandId") Long brandId) {
        BrandInfo brandInfo = brandFacade.getBrandInfo(brandId);
        return ApiResponse.success(BrandV1Dto.BrandResponse.from(brandInfo));
    }
}
