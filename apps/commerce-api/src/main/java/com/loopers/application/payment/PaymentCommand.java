package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentMethod;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public class PaymentCommand {
    public enum PaymentStatus {
        SUCCESS, FAILED;
    }

    public record Payment(String method, Long price, Long cardId) {
        public Payment(String method, Long price, Long cardId) {
            if (method.equals(PaymentMethod.CARD.name()) && cardId == null) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "카드로 결제 시 카드 정보는 필수입니다.");
            }
            this.method = method;
            this.price = price;
            this.cardId = cardId;
        }
    }
}
