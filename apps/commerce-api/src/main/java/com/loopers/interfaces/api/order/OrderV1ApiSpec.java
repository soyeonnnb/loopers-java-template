package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Order V1 API", description = "Order API")
public interface OrderV1ApiSpec {

    @Operation(
            summary = "주문하기",
            description = "주문을 합니다."
    )
    @PostMapping
    ApiResponse<OrderV1Dto.OrderResponse> order(
            @RequestHeader("X-USER-ID") String userId,
            @Schema(name = "주문 정보", description = "주문 정보")
            @RequestBody OrderV1Dto.OrderRequest orderRequest
    );

    @Operation(
            summary = "내 주문 전체 조회",
            description = "내 주문 전체 정보를 조회합니다."
    )
    @GetMapping
    ApiResponse<List<OrderV1Dto.OrderResponse>> getOrderList(
            @RequestHeader("X-USER-ID") String userId,
            @Schema(description = "조회 시작일") @RequestParam("startDate") LocalDate startDate,
            @Schema(description = "조회 종료일") @RequestParam("endDate") LocalDate endDate,
            @Schema(description = "페이지번호") @RequestParam("page") Integer page,
            @Schema(description = "페이지크기") @RequestParam("size") Integer size
    );

    @Operation(
            summary = "내 주문 상세 조회",
            description = "내 주문의 상세 정보를 조회합니다."
    )
    @GetMapping("/{orderId}")
    ApiResponse<OrderV1Dto.OrderResponse> getOrder(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable("orderId") Long orderId
    );

}
