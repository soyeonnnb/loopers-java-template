package com.loopers.domain.product;

import java.util.Optional;

public interface BrandRepository {
    Optional<BrandEntity> findById(Long id);

    BrandEntity save(BrandEntity brandEntity);
}
