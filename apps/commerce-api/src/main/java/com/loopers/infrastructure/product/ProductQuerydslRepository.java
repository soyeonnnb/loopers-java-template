package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductSortOrder;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductQuerydslRepository {
    Page<ProductEntity> findProductsByBrandOrderBySortOrder(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order
            , Pageable pageable);
}
