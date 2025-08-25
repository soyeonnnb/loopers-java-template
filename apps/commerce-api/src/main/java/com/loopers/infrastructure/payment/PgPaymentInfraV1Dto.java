package com.loopers.infrastructure.payment;

import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public class PgPaymentInfraV1Dto {
    public record PgPaymentRequest(String orderId, String cardType, String cardNo, String amount, String callbackUrl) {
        public PgPaymentRequest(String orderId, String cardType, String cardNo, String amount, String callbackUrl) {
            if (orderId == null || orderId.isBlank()) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "PG 결제 발송 시, 주문번호는 필수입니다.");
            }

            if (cardType == null || cardType.isBlank()) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "PG 결제 발송 시, 카드타입은 필수입니다.");
            }

            if (cardNo == null || cardNo.isBlank()) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "PG 결제 발송 시, 카드번호는 필수입니다.");
            }

            if (amount == null || amount.isBlank()) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "PG 결제 발송 시, 주문금액은 필수입니다.");
            }

            if (callbackUrl == null || callbackUrl.isBlank()) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "PG 결제 발송 시, 콜백함수는 필수입니다.");
            }
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.callbackUrl = callbackUrl;
        }

    }

    public record PaymentResponse(Boolean isSuccess, String transactionKey, PaymentV1Dto.TransactionStatusResponse status,
                                  String reason) {
        public PaymentResponse(Boolean isSuccess, String transactionKey, PaymentV1Dto.TransactionStatusResponse status, String reason) {
            this.isSuccess = isSuccess;
            this.transactionKey = transactionKey;
            this.status = status;
            this.reason = reason;
        }

        public static PaymentResponse from(PgPaymentResponse response) {
            return new PaymentResponse(response.meta.result.equals("SUCCESS"), response.data.transactionKey, response.data.status, response.data.reason);
        }
    }

    public record PgPaymentResponse(PgPaymentResult meta, TransactionResponse data) {
    }

    public record PgPaymentInfoResponse(PgPaymentResult meta, TransactionDetailResponse data) {
    }

    public record PgPaymentResult(String result) {
    }

    public record TransactionResponse(String transactionKey, PaymentV1Dto.TransactionStatusResponse status, String reason) {
    }

    public record TransactionDetailResponse(
            String transactionKey,
            String orderId,
            String cardType,
            String cardNo,
            Long amount,
            PaymentV1Dto.TransactionStatusResponse status,
            String reason
    ) {

    }
}
