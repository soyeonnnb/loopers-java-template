package com.loopers.interfaces.api;

import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.*;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.product.ProductV1Dto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final ProductRepository productRepository;

    private final BrandRepository brandRepository;

    private final UserRepository userRepository;
    private final ProductCountRepository productCountRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public ProductV1ApiE2ETest(
            TestRestTemplate testRestTemplate,
            DatabaseCleanUp databaseCleanUp,
            ProductRepository productRepository,
            BrandRepository brandRepository,
            UserRepository userRepository,
            ProductCountRepository productCountRepository,
            LikeRepository likeRepository
    ) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.userRepository = userRepository;
        this.productCountRepository = productCountRepository;
        this.likeRepository = likeRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/product/{productId}")
    @Nested
    class GetProduct {

        private static final String ENDPOINT = "/api/v1/products";

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
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getBody().meta().errorCode()).isEqualTo(GlobalErrorType.NOT_FOUND.getCode())
            );
        }

        @DisplayName("productId 해당하는 상품이 있고,")
        @Nested
        class ValidProductIdProvided {

            @DisplayName("로그인 한 유저가 좋아요한 경우에 상품 정보가 조회된다.")
            @Test
            void returnProductInfo_whenLoginUserLike() {
                // arrange
                String loginId = "la28s5d";
                UserEntity userEntity = userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
                BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity, 1L));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                Long id = productEntity.getId();
                String url = ENDPOINT + "/" + id;

                // act
                HttpHeaders headers = new HttpHeaders();
                headers.add("X-USER-ID", loginId);
                ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
                };
                ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                        testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(id),
                        () -> assertThat(response.getBody().data().name()).isEqualTo(productEntity.getName()),
                        () -> assertTrue(response.getBody().data().isLike())
                );
            }

            @DisplayName("로그인 한 유저가 좋아요하지 않은 경우에 상품 정보가 조회된다.")
            @Test
            void returnProductInfo_whenLoginUserDisLike() {
                // arrange
                String loginId = "la28s5d";
                UserEntity userEntity = userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
                BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);

                Long id = productEntity.getId();
                String url = ENDPOINT + "/" + id;

                // act
                ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
                };
                ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                        testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(loginId), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(id),
                        () -> assertThat(response.getBody().data().name()).isEqualTo(productEntity.getName()),
                        () -> assertFalse(response.getBody().data().isLike())
                );
            }

            @DisplayName("로그아웃 상태인 경우에 상품 정보가 조회된다.")
            @Test
            void returnProductInfo_whenLogout() {
                // arrange
                BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                Long id = productEntity.getId();
                String url = ENDPOINT + "/" + id;

                // act
                ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
                };
                ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                        testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), responseType);

                // assert
                assertAll(
                        () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                        () -> assertThat(response.getBody().data().id()).isEqualTo(id),
                        () -> assertThat(response.getBody().data().name()).isEqualTo(productEntity.getName()),
                        () -> assertFalse(response.getBody().data().isLike())
                );
            }
        }
    }
}
