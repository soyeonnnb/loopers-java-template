package com.loopers.domain.like;

import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductStatus;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LikeModelTest {
    @DisplayName("Like 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("상품이 없으면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenProductIsNull() {
            // arrange
            UserEntity userEntity = new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE");
            // act & assert
            assertThrows(CoreException.class, () ->
                    new LikeEntity(userEntity, null, true)
            );
        }

        @DisplayName("사용자가 없으면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenUserIsNull() {
            // arrange
            BrandEntity brandEntity = new BrandEntity("브랜드");
            ProductEntity productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
            // act & assert
            assertThrows(CoreException.class, () ->
                    new LikeEntity(null, productEntity, true)
            );
        }
    }
}
