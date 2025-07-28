package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Brand V1 API", description = "Brand API")
public interface BrandV1ApiSpec {

    @Operation(
            summary = "브랜드 정보 조회",
            description = "브랜드 정보를 조회합니다."
    )
    ApiResponse<BrandV1Dto.BrandResponse> getBrandInfo(@PathVariable("brandId") Long brandId);
}
