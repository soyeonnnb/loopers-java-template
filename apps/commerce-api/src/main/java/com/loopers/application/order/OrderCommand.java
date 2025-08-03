package com.loopers.application.order;

import com.loopers.domain.product.ProductEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;

public class OrderCommand {
    public record OrderProduct(ProductEntity productEntity, Long quantity) {
        public OrderProduct(ProductEntity productEntity, Long quantity) {
            if (productEntity == null) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 정보가 없습니다.");
            }

            if (quantity == null) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "재고는 null 이 될 수 없습니다.");
            } else if (quantity <= 0) {
                throw new CoreException(GlobalErrorType.BAD_REQUEST, "재고는 1 미만이 될 수 없습니다.");
            }

            this.productEntity = productEntity;
            this.quantity = quantity;
        }
    }
}
