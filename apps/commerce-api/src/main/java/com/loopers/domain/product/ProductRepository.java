package com.loopers.domain.product;

import com.loopers.application.product.ProductSortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<ProductEntity> findById(Long productId);

    ProductEntity save(ProductEntity productEntity);

    Page<ProductEntity> findProductsByBrandOrderBySortOrder(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order, Pageable pageable);

    List<ProductEntity> saveAll(List<ProductEntity> productEntityList);
}
