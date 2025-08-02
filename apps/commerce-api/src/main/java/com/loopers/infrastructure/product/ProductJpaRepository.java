package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    @EntityGraph(attributePaths = {"productCount", "brand"})
    Optional<ProductEntity> findById(Long id);
}
