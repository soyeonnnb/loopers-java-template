package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<ProductEntity> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public ProductEntity save(ProductEntity productEntity) {
        return productJpaRepository.save(productEntity);
    }
}
