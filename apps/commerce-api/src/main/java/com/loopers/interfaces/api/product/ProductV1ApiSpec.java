package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Product V1 API", description = "Product API")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 정보 조회",
            description = "상품 정보를 조회합니다."
    )
    ApiResponse<ProductV1Dto.ProductResponse> getProductInfo(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable("productId") Long productId);
}
