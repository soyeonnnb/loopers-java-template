package com.loopers.interfaces.api;

import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandRepository;
import com.loopers.interfaces.api.product.BrandV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final BrandRepository brandRepository;

    @Autowired
    public BrandV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            BrandRepository brandRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.brandRepository = brandRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    class GetBrand {

        private static final String ENDPOINT = "/api/v1/brands";

        @DisplayName("brandId에 해당하는 브랜드가 있는 경우 브랜드 정보가 조회된다.")
        @Test
        void returnBrandInfo_whenValidBrandIdProvided() {
            // arrange
            String name = "test name";
            BrandEntity brandEntity = new BrandEntity(name);
            brandEntity = brandRepository.save(brandEntity);
            Long id = brandEntity.getId();
            String url = ENDPOINT + "/" + brandEntity.getId();

            // act
            ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(id),
                    () -> assertThat(response.getBody().data().name()).isEqualTo(name)
            );
        }

        @DisplayName("brandId에 해당하는 브랜드가 없는 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void return404NotFound_whenBrandNotFound() {
            // arrange

            // act
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response =
                    testRestTemplate.exchange(ENDPOINT + "/1", HttpMethod.GET, new HttpEntity<>(null), responseType);

            // assert
            assertAll(
                    () -> assertTrue(response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)),
                    () -> assertThat(response.getBody().meta().errorCode()).isEqualTo(GlobalErrorType.NOT_FOUND.getCode())
            );
        }

    }
}
