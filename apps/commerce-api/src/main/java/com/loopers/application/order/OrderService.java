package com.loopers.application.order;

import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderItemEntity;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    @Transactional
    public OrderEntity order(UserEntity user, List<OrderCommand.OrderProduct> itemList, Long totalPrice) {
        // 0. 파라미터 값 체크
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }

        if (itemList.isEmpty()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문하려는 상품은 1개 이상이여야 합니다.");
        }

        if (totalPrice < 0L) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문하려는 금액은 0 이상이여야 합니다.");
        }

        // 1. 상품 확인
        orderDomainService.validateOrderItems(itemList);
        if (!totalPrice.equals(orderDomainService.calculateTotalPrice(itemList))) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 총 합계와 주문 금액이 일치하지 않습니다.");
        }

        // 2. 사용자 포인트 확인 및 사용 & 재고 차감
        orderDomainService.processOrder(user, itemList, totalPrice);

        // 3. 주문 생성
        OrderEntity orderEntity = new OrderEntity(user, totalPrice);
        for (OrderCommand.OrderProduct orderProduct : itemList) {
            OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, orderProduct.productEntity(), orderProduct.quantity());
            orderEntity.addOrderItem(orderItemEntity);
        }
        return orderRepository.save(orderEntity);
    }
}
