package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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

    @Autowired
    private ProductCountRepository productCountRepository;

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
            ProductEntity productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
            ProductCountEntity productCountEntity = new ProductCountEntity(productEntity);
            ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
            productEntity = productRepository.save(productEntity);

            Long id = productEntity.getId();

            // act
            Optional<ProductEntity> result = productService.getProductInfo(id);

            // assert
            ProductEntity finalProductEntity = productEntity;
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                    },
                    () -> assertEquals(result.get().getId(), finalProductEntity.getId()),
                    () -> assertEquals(result.get().getBrand().getId(), brandEntity.getId()),
                    () -> assertEquals(result.get().getName(), finalProductEntity.getName()),
                    () -> assertEquals(result.get().getPrice(), finalProductEntity.getPrice()),
                    () -> assertEquals(result.get().getQuantity(), finalProductEntity.getQuantity()),
                    () -> assertEquals(result.get().getProductCount().getLikeCount(), 0L)
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

    @DisplayName("상품 리스트를 조회할 때")
    @Nested
    class GetProductInfoList {

        @DisplayName("size가 0 이하이면 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {
                -1, 0
        })
        void throws400Exception_whenSizeLessThanEquals0(int size) {
            // arrange

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    productService.getProductInfoList(null, null, size, 1)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST)
            );
        }

        @DisplayName("page가 0 미만이면 에러가 발생한다.")
        @Test
        void throws400Exception_whenPageLessThan0() {
            // arrange

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    productService.getProductInfoList(null, null, 1, -1)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST)
            );
        }

    }
}
