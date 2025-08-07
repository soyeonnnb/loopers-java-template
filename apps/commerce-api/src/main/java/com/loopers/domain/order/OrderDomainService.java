package com.loopers.domain.order;

import com.loopers.application.order.OrderCommand;
import com.loopers.domain.coupon.UserCouponEntity;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDomainService {

    public void validateOrderItems(List<OrderCommand.OrderProduct> itemList) {
        for (OrderCommand.OrderProduct orderProduct : itemList) {
            if (!orderProduct.productEntity().getStatus().equals(ProductStatus.SALE)) {
                throw new CoreException(GlobalErrorType.CONFLICT, "상품이 판매중 상태가 아닙니다.");
            }
        }
    }

    public Long calculateTotalPrice(List<OrderCommand.OrderProduct> itemList) {
        return itemList.stream()
                .mapToLong(item -> item.productEntity().getPrice() * item.quantity())
                .sum();
    }

    public void processOrder(UserEntity user, List<OrderCommand.OrderProduct> itemList, Long totalPrice) {
        user.usePoint(totalPrice);
        itemList.forEach(item -> item.productEntity().decreaseQuantity(item.quantity()));
    }

    public void validateUserOwnsOrder(UserEntity user, OrderEntity order) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new CoreException(GlobalErrorType.FORBIDDEN, "다른 사용자의 주문 정보입니다.");
        }
    }

    public OrderEntity createOrder(UserEntity user, List<OrderCommand.OrderProduct> itemList, Long totalPrice, UserCouponEntity userCoupon) {
        OrderEntity orderEntity = new OrderEntity(user, totalPrice, userCoupon);
        for (OrderCommand.OrderProduct orderProduct : itemList) {
            OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, orderProduct.productEntity(), orderProduct.quantity());
            orderEntity.addOrderItem(orderItemEntity);
        }
        return orderEntity;
    }
}
