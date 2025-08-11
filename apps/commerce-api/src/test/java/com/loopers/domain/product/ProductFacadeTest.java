package com.loopers.domain.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductInfo;
import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ProductFacadeTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ProductCountRepository productCountRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 정보를 조회할 때,")
    @Nested
    class GetProductInfo {
        @DisplayName("상품 정보가 존재할 때")
        @Nested
        class ExistsProduct {
            @DisplayName("로그인을 하고 좋아요를 한 경우, 상품 정보가 반환된다.")
            @Test
            void returnBrandInfo_whenValidProductIdAndLike() {
                // arrange
                String loginId = "la28s5d";
                UserEntity userEntity = userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));

                BrandEntity brandEntity = brandRepository.save(new BrandEntity("brand name"));
                ProductEntity productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 1L);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productEntity = productRepository.save(productEntity);

                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(loginId, id);

                // assert
                ProductEntity finalProductEntity = productEntity;
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), finalProductEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), finalProductEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), finalProductEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), finalProductEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), finalProductEntity.getDescription());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().id(), brandEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().name(), brandEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.isLike(), true);
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.totalLikes(), 1L);
                        }
                );
            }

            @DisplayName("로그인을 하고 좋아요를 하지 않은 경우, 상품 정보가 반환된다.")
            @Test
            void returnBrandInfo_whenValidProductIdAndDisLike() {
                // arrange
                String loginId = "la28s5d";
                userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
                UserEntity userEntity = userRepository.save(new UserEntity(loginId + "1", "password", "la28s5d2@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));

                BrandEntity brandEntity = brandRepository.save(new BrandEntity("brand name"));
                ProductEntity productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 1L);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productEntity = productRepository.save(productEntity);

                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(loginId, id);

                // assert
                ProductEntity finalProductEntity = productEntity;
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), finalProductEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), finalProductEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), finalProductEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), finalProductEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), finalProductEntity.getDescription());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().id(), brandEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().name(), brandEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.isLike(), false);
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.totalLikes(), 1L);
                        }
                );
            }

            @DisplayName("로그아웃을 한 경우, 상품 정보가 반환된다.")
            @Test
            void returnBrandInfo_whenValidProductIdAndLogout() {
                // arrange
                String loginId = "la28s5d";
                UserEntity userEntity = userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));

                BrandEntity brandEntity = brandRepository.save(new BrandEntity("brand name"));
                ProductEntity productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity, 1L);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productEntity = productRepository.save(productEntity);
                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(null, id);

                // assert
                ProductEntity finalProductEntity = productEntity;
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), finalProductEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), finalProductEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), finalProductEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), finalProductEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), finalProductEntity.getDescription());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().id(), brandEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.brandInfo().name(), brandEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.isLike(), false);
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.totalLikes(), 1L);
                        }
                );
            }
        }

        @DisplayName("상품 정보가 존재하지 않을 때")
        @Nested
        class NotExistsProduct {

            @DisplayName("로그인 시, 404 에러가 발생한다.")
            @Test
            void throws404Exception_whenInvalidProductIdAndLogin() {
                // arrange
                String loginId = "la28s5d";
                userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));

                // act
                CoreException result = assertThrows(CoreException.class, () ->
                        productFacade.getProductInfo(loginId, 1L)
                );

                // assert
                assertAll(
                        () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.NOT_FOUND)
                );
            }

            @DisplayName("비로그인 시, 404 에러가 발생한다.")
            @Test
            void throws404Exception_whenInvalidProductIdAndLogout() {
                // arrange

                // act
                CoreException result = assertThrows(CoreException.class, () ->
                        productFacade.getProductInfo(null, 1L)
                );

                // assert
                assertAll(
                        () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.NOT_FOUND)
                );
            }
        }

    }

    @DisplayName("상품 리스트를 조회할 때")
    @Nested
    class GetProductInfoList {

        @DisplayName("userId에 해당하는 사용자가 없는 경우, 401 에러가 발생한다.")
        @Test
        void return401Exception_whenInvalidUserId() {
            // arrange


            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    productFacade.getProductInfoList("la28s5d", null, null, 1, 1)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.NOT_FOUND)
            );
        }

        @DisplayName("brandId가 null인 경우, 브랜드 상관 없이 상품이 조회된다.")
        @Test
        void returnProductInfoList_whenBrandIdIsNull() {
            // arrange
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            for (int i = 0; i < 20; i++) {
                ProductEntity productEntity = new ProductEntity(brandEntity, "상품" + i, 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, i + 2, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productRepository.save(productEntity);
            }

            // act
            List<ProductInfo> result = productFacade.getProductInfoList(null, null, null, 10, 1);

            // assert
            assertAll(
                    () -> assertEquals(result.size(), 10)
            );
        }

        @DisplayName("brandId에 해당하는 브랜드가 없는 경우, 404 에러가 발생한다.")
        @Test
        void throws404Exception_whenInvalidBrandId() {
            // arrange

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    productFacade.getProductInfoList("la28s5d", 1L, null, 1, 1)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.NOT_FOUND)
            );
        }

        @DisplayName("size가 0 이하이면 에러가 발생한다.")
        @ParameterizedTest
        @ValueSource(ints = {
                -1, 0
        })
        void throws400Exception_whenSizeLessThanEquals0(int size) {
            // arrange

            // act
            CoreException result = assertThrows(CoreException.class, () ->
                    productFacade.getProductInfoList(null, null, null, size, 1)
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
                    productFacade.getProductInfoList(null, null, null, 1, -1)
            );

            // assert
            assertAll(
                    () -> assertThat(result.getErrorType()).isEqualTo(GlobalErrorType.BAD_REQUEST)
            );
        }

    }

}
