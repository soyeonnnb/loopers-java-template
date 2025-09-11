package com.loopers.interfaces.api.ranking;

import com.loopers.application.product.ProductInfo;
import com.loopers.application.ranking.RankingFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rankings")
public class RankingV1Controller implements RankingV1ApiSpec {

    private final RankingFacade rankingFacade;

    @Override
    @GetMapping
    public ApiResponse<List<ProductV1Dto.ProductResponse>> getProductRankingList(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "page", defaultValue = "1") Integer page
    ) {
        List<ProductInfo> response = rankingFacade.getRankingList(userId, date, page, size);
        return ApiResponse.success(response.stream().map(ProductV1Dto.ProductResponse::from).toList());
    }
}
