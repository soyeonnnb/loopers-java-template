package com.loopers.infrastructure.product;

import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
public class BrandRepositoryImpl implements BrandRepository {
    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Optional<BrandEntity> findById(Long id) {
        return brandJpaRepository.findById(id);
    }

    @Override
    public BrandEntity save(BrandEntity brandEntity) {
        return brandJpaRepository.save(brandEntity);
    }
}
