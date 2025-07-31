package com.loopers.application.order;

import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

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

}
