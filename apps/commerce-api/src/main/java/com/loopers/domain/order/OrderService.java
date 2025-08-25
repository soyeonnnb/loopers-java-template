package com.loopers.domain.order;

import com.loopers.application.order.OrderCommand;
import com.loopers.application.payment.PaymentCommand;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final PaymentService paymentService;

    @Transactional
    public OrderEntity order(UserEntity user, List<OrderCommand.OrderProduct> itemList, Long totalPrice, UserCouponEntity userCoupon, PaymentCommand.Payment paymentCommand) {
        // 0. 파라미터 값 체크
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }

        if (itemList.isEmpty()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문하려는 상품은 1개 이상이여야 합니다.");
        }

        if (totalPrice == null || totalPrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문하려는 금액은 0 이상이여야 합니다.");
        }

        // 2. 주문 생성
        OrderEntity orderEntity = orderDomainService.createOrder(user, itemList, totalPrice, userCoupon);
        paymentService.addPaymentToOrder(orderEntity, paymentCommand);

        return orderRepository.save(orderEntity);
    }


    @Transactional(readOnly = true)
    public Page<OrderEntity> getUserOrderList(UserEntity user, LocalDate startDate, LocalDate endDate, Integer size, Integer page) {
        // 0. 파라미터 값 체크
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }

        // 1. 시작일 / 종료일 확인
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.");
        }

        if (startDate == null) {
            startDate = LocalDate.of(1, 1, 1);
        }

        if (endDate == null) {
            endDate = LocalDate.of(9999, 12, 31);
        }

        ZoneId systemZone = ZoneId.systemDefault();

        // 2. page 생성
        if (page == null) {
            page = 0;
        } else if (page < 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "페이지는 최소 0 이상이여야 합니다.");
        }
        if (size == null) {
            size = 20;
        } else if (size < 1) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "페이지 크기는 최소 1 이상이여야 합니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findOrdersByUserAndStartDateAndEndDateOrderByCreatedAtDesc(user, startDate.atStartOfDay(systemZone), endDate.plusDays(1L).atStartOfDay(systemZone), pageable);
    }

    public Optional<OrderEntity> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    public OrderEntity getOrderForPay(String transactionKey, String orderUUID) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findByUuidAndPaymentTransactionKeyWithLock(orderUUID, transactionKey);
        if (orderEntityOptional.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "해당 uuid와 transactionKey에 맞는 주문 데이터가 존재하지 않습니다.");
        }

        OrderEntity order = orderEntityOptional.get();
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "이미 처리된 주문입니다.");
        }
        return order;
    }

    @Transactional
    public void rollbackOrder(OrderEntity order) {
        for (OrderItemEntity orderItem : order.getItems()) {
            orderItem.getProduct().increaseQuantity(orderItem.getQuantity());
        }
    }

    @Transactional
    public List<OrderEntity> getPendingOrderList(ZonedDateTime startAt, ZonedDateTime endAt) {
        return orderRepository.findOrdersByStatusAndCreatedAtBetweenWithCardPay(OrderStatus.PENDING, startAt, endAt);
    }
}
