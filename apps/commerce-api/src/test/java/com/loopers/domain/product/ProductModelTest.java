package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductModelTest {
    @DisplayName("Product 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("브랜드가 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenBrandIsNull() {
            // arrange

            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(null, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("상품명이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsNull() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, null, 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("상품명이 비어있으면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsEmpty() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, "", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("상품 가격이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenPriceIsNull() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, "상품", null, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("상품 가격이 음수면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenPriceIsNegativeNumber() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, "상품", -1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("재고가 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenQuantityIsNull() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, "상품", 1L, null, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }

        @DisplayName("재고가 음수면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenQuantityIsNegativeNumber() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new ProductEntity(brandEntity, "상품", 1L, -1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0))
            );
        }
    }

}
