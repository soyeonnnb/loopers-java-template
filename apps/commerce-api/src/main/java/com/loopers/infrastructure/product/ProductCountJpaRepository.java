package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCountJpaRepository extends JpaRepository<ProductCountEntity, Long> {
}
