package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCountEntity;
import com.loopers.domain.product.ProductCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductCountRepositoryImpl implements ProductCountRepository {

    private final ProductCountJpaRepository productCountJpaRepository;

    @Override
    public Optional<ProductCountEntity> findById(Long productId) {
        return productCountJpaRepository.findById(productId);
    }

    @Override
    public ProductCountEntity save(ProductCountEntity productCount) {
        return productCountJpaRepository.save(productCount);
    }

}
