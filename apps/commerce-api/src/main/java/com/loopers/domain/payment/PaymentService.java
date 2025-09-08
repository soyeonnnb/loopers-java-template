package com.loopers.domain.payment;

import com.loopers.application.event.EventPublisher;
import com.loopers.application.payment.PaymentGateway;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.user.UserService;
import com.loopers.infrastructure.payment.PgPaymentInfraV1Dto;
import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.interfaces.listener.payment.PaymentFailEvent;
import com.loopers.interfaces.listener.payment.PaymentSuccessEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserService userService;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final PgPayService pgPayService;
    private final PaymentGateway paymentGateway;
    private final OrderService orderService;
    private final EventPublisher eventPublisher;


    @Transactional(readOnly = true)
    public Optional<CardEntity> getCardInfo(Long cardId) {
        if (cardId == null) return Optional.empty();
        else return cardRepository.findById(cardId);
    }

    @Transactional
    public void addPaymentToOrder(OrderEntity order, String method, Long cardId) {
        CardEntity card = getCardInfo(cardId).orElse(null);
        if (card != null && !card.getUser().getId().equals(order.getUser().getId())) {
            throw new CoreException(GlobalErrorType.FORBIDDEN, "사용자 카드가 아닙니다.");
        }
        PaymentEntity paymentEntity = new PaymentEntity(order, PaymentMethod.from(method), card, PaymentStatus.PENDING);
        order.addPayment(paymentEntity);
    }

    @Transactional
    public Boolean payment(Long userId, Long paymentId, String orderUuid, Long orderId) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "결제 정보가 없습니다."));
        switch (paymentEntity.getMethod()) {
            case POINT -> {
                userService.usePoint(userId, paymentEntity.getOrder().getTotalPrice());
                eventPublisher.publish(new PaymentSuccessEvent(paymentId, orderId, userId, orderUuid, paymentEntity.getOrder().getTotalPrice(), paymentEntity.getMethod().name(), LocalDateTime.now()));
                return true;
            }
            case CARD -> {
                PgPaymentInfraV1Dto.PaymentResponse response = pgPayService.pay(userId, orderUuid, paymentEntity.getCard().getUser().getName(), paymentEntity.getCard().getNumber(), paymentEntity.getOrder().getTotalPrice());
                if (response.isSuccess()) {
                    paymentEntity.updateTransactionKey(response.transactionKey());
                } else {
                    eventPublisher.publish(new PaymentFailEvent(paymentId, orderId, userId, response.reason(), LocalDateTime.now()));
                }
                return response.isSuccess();

            }
        }
        return true;
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
                eventPublisher.publish(new PaymentSuccessEvent(order.getPayment().getId(), order.getId(), order.getUser().getId(), order.getUuid(), order.getTotalPrice(), order.getPayment().getMethod().name(), LocalDateTime.now()));
            } else {
                eventPublisher.publish(new PaymentFailEvent(order.getPayment().getId(), order.getId(), order.getUser().getId(), result.data().reason(), LocalDateTime.now()));
            }
        } catch (CoreException e) {
            log.info("에러가 발생했습니다. 메세지: {}", e.getMessage());
        }
    }

    @Transactional
    public void paymentFail(Long paymentId, String reason) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "결제 정보가 없습니다."));
        orderService.rollbackOrder(paymentEntity.getOrder());
        paymentEntity.getOrder().payFailed(reason);
    }

    @Transactional
    public void paymentSuccess(Long paymentId) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "결제 정보가 없습니다."));
        paymentEntity.getOrder().paySuccess();
    }
}
