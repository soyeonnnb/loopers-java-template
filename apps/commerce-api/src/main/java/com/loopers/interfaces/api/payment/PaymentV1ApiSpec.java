package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "Payment V1 API", description = "Payment API")
public interface PaymentV1ApiSpec {

    @Operation(
            summary = "결제 콜백 함수",
            description = "결제 성공시 오는 콜백함수 입니다."
    )
    @PostMapping
    ApiResponse<String> callback(PaymentV1Dto.PaymentCallbackRequest request);

    @Operation(
            summary = "결제 현황 수동 패치",
            description = "결제 성공시 오는 콜백함수 입니다."
    )
    @PostMapping("/fetch")
    ApiResponse<String> fetch(@RequestParam LocalDateTime startAt, @RequestParam LocalDateTime endAt);
}
