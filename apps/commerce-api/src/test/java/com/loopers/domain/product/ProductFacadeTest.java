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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

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
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity, 1L));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);

                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(loginId, id);

                // assert
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), productEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), productEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), productEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), productEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), productEntity.getDescription());
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

            // TODO: 좋아요X + 로그인 상태 추가
            @DisplayName("로그인을 하고 좋아요를 한 경우, 상품 정보가 반환된다.")
            @Test
            void returnBrandInfo_whenValidProductIdAndDisLike() {
                // arrange
                String loginId = "la28s5d";
                userRepository.save(new UserEntity(loginId, "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
                UserEntity userEntity = userRepository.save(new UserEntity(loginId + "1", "password", "la28s5d2@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));

                BrandEntity brandEntity = brandRepository.save(new BrandEntity("brand name"));
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity, 1L));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);

                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(loginId, id);

                // assert
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), productEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), productEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), productEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), productEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), productEntity.getDescription());
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
                ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));
                ProductCountEntity productCountEntity = productCountRepository.save(new ProductCountEntity(productEntity, 1L));
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                Long id = productEntity.getId();

                likeRepository.save(new LikeEntity(userEntity, productEntity, true));

                // act
                ProductInfo productInfo = productFacade.getProductInfo(null, id);

                // assert
                assertAll(
                        () -> assertThat(productInfo).isNotNull(),
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.id(), productEntity.getId());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.name(), productEntity.getName());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.price(), productEntity.getPrice());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.quantity(), productEntity.getQuantity());
                        },
                        () -> {
                            assert productInfo != null;
                            assertEquals(productInfo.description(), productEntity.getDescription());
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

}
