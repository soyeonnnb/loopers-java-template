package com.loopers.application.payment;

import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentMethod;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentFacade {
    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public void paymentCallback(String transactionKey, String orderUUID, PaymentStatus status, String reason) {
        if (transactionKey == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 확인 시 트랜젝션 키는 필수입니다.");
        }
        if (orderUUID == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 확인 시 주문 UUID는 필수입니다.");
        }
        if (status == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "결제 확인 시 결제 결과는 필수입니다.");
        }

        OrderEntity order = orderService.getOrderForPay(transactionKey, orderUUID);

        if (status.equals(PaymentStatus.SUCCESS)) {
            paymentService.pay(order);
        } else {
            paymentService.fail(order, reason);
            orderService.rollbackOrder(order);
        }
    }

    @Transactional
    public void paymentFetch(LocalDateTime startAt, LocalDateTime endAt) {
        log.info("{}시 {}분 ~ {}시 {}분 결제 정보 업데이트 안된 주문 패치 작업 시작", startAt.getHour(), startAt.getMinute(), endAt.getHour(), endAt.getMinute());
        ZonedDateTime start = ZonedDateTime.of(startAt, ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.of(endAt, ZoneId.systemDefault());
        List<OrderEntity> orderEntityList = orderService.getPendingOrderList(start, end);

        for (OrderEntity orderEntity : orderEntityList) {
            if (orderEntity.getPayment().getMethod().equals(PaymentMethod.CARD)) {
                log.info("{}번 주문 결제정보 패치 시작", orderEntity.getId());
                paymentService.fetchPaymentInfo(orderEntity);
            }
        }
    }
}
