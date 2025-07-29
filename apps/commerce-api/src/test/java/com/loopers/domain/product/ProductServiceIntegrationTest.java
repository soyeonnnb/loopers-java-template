package com.loopers.domain.product;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 정보를 조회할 때,")
    @Nested
    class GetProductInfo {
        @DisplayName("정보가 존재하면, 상품 객체가 반환된다.")
        @Test
        void returnProductInfo_whenValidProductId() {
            // arrange
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("test name"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품명", 100L, 100L, ProductStatus.SALE, "설명"));
            Long id = productEntity.getId();

            // act
            Optional<ProductEntity> result = productService.getProductInfo(id);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                    },
                    () -> assertEquals(result.get().getId(), productEntity.getId()),
                    () -> assertEquals(result.get().getBrand().getId(), brandEntity.getId()),
                    () -> assertEquals(result.get().getName(), productEntity.getName()),
                    () -> assertEquals(result.get().getPrice(), productEntity.getPrice()),
                    () -> assertEquals(result.get().getQuantity(), productEntity.getQuantity())
            );
        }

        @DisplayName("정보가 존재하지 않으면, null이 반환된다.")
        @Test
        void returnNull_whenInvalidProductId() {
            // arrange

            // act
            Optional<ProductEntity> optionalProductEntity = productService.getProductInfo(1L);

            // assert
            assertAll(
                    () -> assertTrue(optionalProductEntity.isEmpty())
            );
        }
    }

}
