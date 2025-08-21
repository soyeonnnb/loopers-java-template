package com.loopers.infrastructure.payment;

import com.loopers.application.payment.PaymentGateway;
import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {

    private final RestTemplate restTemplate;

    @Override
    @Retry(name = "pgRequest", fallbackMethod = "pgFallback")
    @CircuitBreaker(name = "pgRequest", fallbackMethod = "pgFallback")
    public PgPaymentInfraV1Dto.PaymentResponse requestPayment(Long userId, String orderUUID, String cardType, String cardNo, Long amount) {
        log.info("PG 결제 요청 시작 - userId: {}, orderUUID: {}", userId, orderUUID);

        // 유효성 검증
        validatePaymentRequest(userId, orderUUID, cardType, cardNo, amount);

        String url = "http://localhost:8082/api/v1/payments";
        String callbackUrl = "http://localhost:8080/api/v1/payments/callback";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", userId.toString());
        PgPaymentInfraV1Dto.PgPaymentRequest paymentRequest = new PgPaymentInfraV1Dto.PgPaymentRequest(
                orderUUID,
                cardType,
                cardNo,
                amount.toString(),
                callbackUrl
        );

        HttpEntity<PgPaymentInfraV1Dto.PgPaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<PgPaymentInfraV1Dto.PgPaymentResponse> response = restTemplate.postForEntity(
                url,
                request,
                PgPaymentInfraV1Dto.PgPaymentResponse.class
        );
        return PgPaymentInfraV1Dto.PaymentResponse.from(response.getBody());
    }

    @Override
    public PgPaymentInfraV1Dto.PgPaymentInfoResponse getPaymentInfo(Long userId, String transactionKey) {
        log.info("PG 결제 정보 확인 요청 시작 - userId: {}, transactionKey: {}", userId, transactionKey);

        if (userId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 정보 확인 요청 시 사용자 ID는 필수입니다.");
        }

        if (transactionKey == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 정보 확인 요청 시 transactionKey는 필수입니다.");
        }

        String url = "http://localhost:8082/api/v1/payments/" + transactionKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", userId.toString());

        HttpEntity<PgPaymentInfraV1Dto.TransactionDetailResponse> request = new HttpEntity<>(headers);

        ResponseEntity<PgPaymentInfraV1Dto.PgPaymentInfoResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                PgPaymentInfraV1Dto.PgPaymentInfoResponse.class
        );

        return response.getBody();
    }

    private void validatePaymentRequest(Long userId, String orderUUID, String cardType, String cardNo, Long amount) {
        if (userId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 요청 시 사용자 ID는 필수입니다.");
        }

        if (orderUUID == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 요청 시 주문 UUID는 필수입니다.");
        }

        if (cardType == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 요청 시 카드 타입은 필수입니다.");
        }

        if (cardNo == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 요청 시 카드 번호는 필수입니다.");
        }

        if (amount == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "pg 결제 요청 시 금액은 필수입니다.");
        } else if (amount <= 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "금액은 1 이상이여야 합니다.");
        }
    }

    public PgPaymentInfraV1Dto.PaymentResponse pgFallback(Long userId, String orderUUID, String cardType, String cardNo, Long amount, Exception ex) {
        log.error("PG 결제 요청 fallback 실행 - userId: {}, orderUUID: {}, error: {}",
                userId, orderUUID, ex.getMessage(), ex);
        return new PgPaymentInfraV1Dto.PaymentResponse(false, null, PaymentV1Dto.TransactionStatusResponse.FAILED, "PG 시스템에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
