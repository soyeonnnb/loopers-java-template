package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderInfo;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<OrderV1Dto.OrderResponse> order(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody OrderV1Dto.OrderRequest orderRequest) {
        OrderInfo orderInfo = orderFacade.order(userId, orderRequest);
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(orderInfo));
    }

    @Override
    @GetMapping
    public ApiResponse<List<OrderV1Dto.OrderResponse>> getOrderList(
            @RequestHeader("X-USER-ID") String userId,
            @Schema(description = "조회 시작일") @RequestParam("startDate") LocalDate startDate,
            @Schema(description = "조회 종료일") @RequestParam("endDate") LocalDate endDate,
            @Schema(description = "페이지번호") @RequestParam("page") Integer page,
            @Schema(description = "페이지크기") @RequestParam("size") Integer size
    ) {
        List<OrderInfo> orderInfoList = orderFacade.getUserInfoList(userId, startDate, endDate, page, size);
        return ApiResponse.success(orderInfoList.stream().map(OrderV1Dto.OrderResponse::from).toList());
    }

    @Override
    @GetMapping("/{orderId}")
    public ApiResponse<OrderV1Dto.OrderResponse> getOrder(
            @RequestHeader("X-USER-ID") String userId,
            @Schema(description = "주문번호") @PathVariable("orderId") Long orderId
    ) {
        OrderInfo orderInfo = orderFacade.getUserOrder(userId, orderId);
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(orderInfo));
    }
}
