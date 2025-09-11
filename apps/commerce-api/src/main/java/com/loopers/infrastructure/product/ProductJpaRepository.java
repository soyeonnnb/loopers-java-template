package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    @EntityGraph(attributePaths = {"productCount", "brand"})
    Optional<ProductEntity> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"productCount", "brand"})
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> getProductInfoWithLock(@Param("id") Long id);

    @EntityGraph(attributePaths = {"productCount", "brand"})
    @Query("SELECT p FROM ProductEntity p WHERE p.id in :list")
    List<ProductEntity> findAllByIdIn(@Param("list") List<Long> productIdList);
}
