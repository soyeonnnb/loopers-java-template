package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentCommand;
import com.loopers.application.payment.PaymentGateway;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.infrastructure.payment.PgPaymentInfraV1Dto;
import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserService userService;
    private final CardRepository cardRepository;
    private final PgPayService pgPayService;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    @Transactional(readOnly = true)
    public Optional<CardEntity> getCardInfo(Long cardId) {
        if (cardId == null) return Optional.empty();
        else return cardRepository.findById(cardId);
    }

    public void addPaymentToOrder(OrderEntity order, PaymentCommand.Payment paymentCommand) {
        CardEntity card = getCardInfo(paymentCommand.cardId()).orElse(null);
        if (card != null && !card.getUser().getId().equals(order.getUser().getId())) {
            throw new CoreException(GlobalErrorType.FORBIDDEN, "사용자 카드가 아닙니다.");
        }
        PaymentEntity paymentEntity = new PaymentEntity(order, PaymentMethod.from(paymentCommand.method()), card, PaymentStatus.PENDING);
        order.addPayment(paymentEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean payment(UserEntity user, OrderEntity order) {
        switch (order.getPayment().getMethod()) {
            case POINT -> {
                userService.usePoint(user, order.getTotalPrice());
                order.getPayment().updateStatus(PaymentStatus.SUCCESS);
                return true;
            }
            case CARD -> {
                return pgPayService.pay(order);
            }
        }
        return true;
    }

    @Transactional
    public void pay(OrderEntity order) {
        order.paySuccess();
    }

    @Transactional
    public void fail(OrderEntity order, String reason) {
        order.payFailed(reason);
    }

    @Transactional
    public void fetchPaymentInfo(OrderEntity order) {
        try {
            PgPaymentInfraV1Dto.PgPaymentInfoResponse result = paymentGateway.getPaymentInfo(order.getUser().getId(), order.getPayment().getTransactionKey());
            if (!result.meta().result().equals("SUCCESS")) {
                return;
            }

            if (!result.data().orderId().equals(order.getUuid())) {
                log.warn("트랜젝션 번호와 주문 Uuid가 일치하지 않습니다. [orderId={}, orderUUID={}, transactionKey={}]", order.getId(), order.getUuid(), order.getPayment().getTransactionKey());
            } else if (result.data().status().equals(PaymentV1Dto.TransactionStatusResponse.SUCCESS)) {
                pay(order);
            } else {
                fail(order, result.data().reason());
            }
        } catch (CoreException e) {
            log.info("에러가 발생했습니다. 메세지: {}", e.getMessage());
        }
    }
}
