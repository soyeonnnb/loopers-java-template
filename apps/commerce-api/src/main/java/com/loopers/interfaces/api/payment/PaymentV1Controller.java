package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

    private final PaymentFacade paymentFacade;

    @Override
    @PostMapping("/callback")
    public ApiResponse<String> callback(@RequestBody PaymentV1Dto.PaymentCallbackRequest request) {
        paymentFacade.paymentCallback(request.transactionKey(), request.orderId(), PaymentStatus.from(request.status().name()), request.reason());
        return ApiResponse.success("콜백 함수 호출에 성공하였습니다.");
    }

    @Override
    @PostMapping("/fetch")
    public ApiResponse<String> fetch(@RequestParam LocalDateTime startAt, @RequestParam LocalDateTime endAt) {
        paymentFacade.paymentFetch(startAt, endAt);
        return ApiResponse.success("결제 정보 패치 작업에 성공하였습니다.");
    }

}
