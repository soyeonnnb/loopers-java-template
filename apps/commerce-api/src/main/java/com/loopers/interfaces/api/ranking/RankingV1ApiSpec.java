package com.loopers.interfaces.api.ranking;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.ProductV1Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Ranking V1 API", description = "Ranking API")
public interface RankingV1ApiSpec {

    @Operation(
            summary = "랭킹 조회",
            description = "랭킹 리스트를 조회합니다."
    )
    ApiResponse<List<ProductV1Dto.ProductResponse>> getProductRankingList(
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "page", defaultValue = "1") Integer page
    );


}
