package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrandModelTest {
    @DisplayName("Brand 객체를 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("브랜드 명이 비어있으면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsEmpty() {
            // arrange
            String name = "";
            // act & assert
            assertThrows(CoreException.class, () ->
                    new BrandEntity(name)
            );
        }

        @DisplayName("브랜드 명이 null이면 객체 생성에 실패한다.")
        @Test
        void throwsBadRequestException_whenNameIsNull() {
            // arrange
            String name = null;
            // act & assert
            assertThrows(CoreException.class, () ->
                    new BrandEntity(name)
            );
        }
    }
}
