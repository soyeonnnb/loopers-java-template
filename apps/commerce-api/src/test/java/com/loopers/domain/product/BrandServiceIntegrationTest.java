package com.loopers.domain.product;

import com.loopers.application.product.BrandFacade;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class BrandServiceIntegrationTest {
    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private BrandFacade brandFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 정보를 조회할 때,")
    @Nested
    class GetBrandInfo {
        @DisplayName("정보가 존재하면, 브랜드 정보가 반환된다.")
        @Test
        void returnBrandInfo_whenValidBrandId() {
            // arrange
            String name = "브랜드";
            BrandEntity brandEntity = brandRepository.save(new BrandEntity(name));
            Long id = brandEntity.getId();

            // act
            Optional<BrandEntity> result = brandService.getBrandInfo(id);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertTrue(result.isPresent());
                    },
                    () -> {
                        assert Objects.requireNonNull(result).isPresent();
                        assertThat(result.get().getName().equals(name));
                    }
            );
        }

        @DisplayName("정보가 존재하지 않으면, null이 반환된다.")
        @Test
        void returnNull_whenInvalidBrandId() {
            // arrange

            // act
            Optional<BrandEntity> brandEntityOptional = brandService.getBrandInfo(1L);

            // assert
            assertAll(
                    () -> assertTrue(brandEntityOptional.isEmpty())
            );
        }
    }

}
