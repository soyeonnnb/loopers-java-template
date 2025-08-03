package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductSortOrder;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQuerydslRepository productQuerydslRepository;

    @Override
    public Optional<ProductEntity> findById(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public ProductEntity save(ProductEntity productEntity) {
        return productJpaRepository.save(productEntity);
    }

    @Override
    public Page<ProductEntity> findProductsByBrandOrderBySortOrder(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order, Pageable pageable) {
        return productQuerydslRepository.findProductsByBrandOrderBySortOrder(optionalBrandEntity, order, pageable);
    }

    @Override
    public List<ProductEntity> saveAll(List<ProductEntity> productEntityList) {
        return productJpaRepository.saveAll(productEntityList);
    }
}
