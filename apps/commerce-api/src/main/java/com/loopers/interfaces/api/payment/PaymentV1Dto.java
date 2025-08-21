package com.loopers.interfaces.api.payment;

import jakarta.validation.constraints.NotBlank;

public class PaymentV1Dto {
    public enum TransactionStatusResponse {
        PENDING,
        SUCCESS,
        FAILED
    }

    public record PaymentRequest(@NotBlank String method, Long cardId) {

    }

    public record PaymentCallbackRequest(String transactionKey, String orderId, String cardType, String cardNo, Long amount,
                                         TransactionStatusResponse status, String reason) {

    }

    public record PaymentCallbackResponse() {
    }
}
