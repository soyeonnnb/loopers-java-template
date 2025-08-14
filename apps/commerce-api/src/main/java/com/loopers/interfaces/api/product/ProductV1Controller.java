package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductInfo;
import com.loopers.application.product.ProductSortOrder;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping
    public ApiResponse<List<ProductV1Dto.ProductResponse>> getProductList(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "order", required = false) ProductSortOrder order,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
        List<ProductInfo> response = productFacade.getProductInfoList(userId, brandId, order, size, page);
        return ApiResponse.success(response.stream().map(ProductV1Dto.ProductResponse::from).toList());
    }
}
