package com.loopers.application.order;

import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderEntity;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderDomainService orderDomainService;

    @Transactional
    public OrderInfo order(String userId, OrderV1Dto.OrderRequest request) {
        // 1. 사용자 정보 확인
        if (userId == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 ID 정보가 없습니다.");
        }

        UserEntity user = userService.getUserInfo(userId).orElseThrow(() -> new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다."));

        // 2. 상품 정보 확인 및 재고 확인
        List<OrderCommand.OrderProduct> itemList = new ArrayList<>();
        for (OrderV1Dto.ProductOrderRequest productOrderRequest : request.items()) {
            ProductEntity productEntity = productService.getProductInfo(productOrderRequest.id()).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다."));
            itemList.add(new OrderCommand.OrderProduct(productEntity, productOrderRequest.quantity()));
        }

        return OrderInfo.from(orderService.order(user, itemList, request.totalPrice()));
    }

    public List<OrderInfo> getUserInfoList(String userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        // 1. 사용자 정보 확인
        if (userId == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 ID 정보가 없습니다.");
        }
        UserEntity user = userService.getUserInfo(userId).orElseThrow(() -> new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다."));

        // 2. 시작일 / 종료일 확인
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "검색 시작 날짜는 검색 마지막날짜 이전이여야 합니다.");
        }


        // 3. 주문 리스트 조회
        Page<OrderEntity> orderEntityPages = orderService.getUserOrderList(user, startDate, endDate, size, page);
        return orderEntityPages.get().map(OrderInfo::from).toList();
    }

    public OrderInfo getUserOrder(String userId, Long orderId) {
        // 1. 사용자 정보 확인
        if (userId == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 ID 정보가 없습니다.");
        }
        UserEntity user = userService.getUserInfo(userId).orElseThrow(() -> new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다."));

        // 2. 주문 정보 조회
        if (orderId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "주문 ID는 필수입니다.");
        }
        OrderEntity orderEntity = orderService.getOrder(orderId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "주문 정보가 없습니다."));

        // 3. 사용자 주문인지 확인
        orderDomainService.validateUserOwnsOrder(user, orderEntity);

        return OrderInfo.from(orderEntity);
    }
}
