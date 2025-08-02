package com.loopers.domain.product;

import java.util.Optional;

public interface ProductCountRepository {
    Optional<ProductCountEntity> findById(Long id);

    ProductCountEntity save(ProductCountEntity productCount);
}
