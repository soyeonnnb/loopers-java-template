package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductInfo;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductResponse> getProductInfo(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable("productId") Long productId) {
        ProductInfo response = productFacade.getProductInfo(userId, productId);
        return ApiResponse.success(ProductV1Dto.ProductResponse.from(response));
    }
}
