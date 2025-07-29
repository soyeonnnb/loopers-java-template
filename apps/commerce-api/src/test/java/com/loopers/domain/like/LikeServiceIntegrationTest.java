package com.loopers.domain.like;

import com.loopers.application.product.BrandFacade;
import com.loopers.domain.product.*;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LikeServiceIntegrationTest {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private LikeService likeService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private BrandFacade brandFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("좋아요 할 때,")
    @Nested
    class Like {
        @DisplayName("이미 좋아요 했다면, 좋아요된 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenAlreadyLike() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            LikeEntity likeEntity = likeRepository.save(new LikeEntity(userEntity, productEntity, true));

            // act
            LikeEntity result = likeService.like(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertEquals(likeEntity.getId(), result.getId());
                    },
                    () -> {
                        assert result != null;
                        assertTrue(result.getIsLike());
                    }
            );
        }

        @DisplayName("이미 좋아요를 하지 않은적이 있다면, 좋아요된 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenDisLike() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            LikeEntity likeEntity = likeRepository.save(new LikeEntity(userEntity, productEntity, false));

            // act
            LikeEntity result = likeService.like(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertEquals(likeEntity.getId(), result.getId());
                    },
                    () -> {
                        assert result != null;
                        assertTrue(result.getIsLike());
                    }
            );
        }

        @DisplayName("이미 좋아요를 한 적이 없다면, 좋아요된 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenHaventLiked() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            // act
            LikeEntity result = likeService.like(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertTrue(result.getIsLike());
                    }
            );
        }
    }

    @DisplayName("좋아요 취소할 때,")
    @Nested
    class DisLike {
        @DisplayName("이전에 좋아요 했다면, 좋아요하지 않은 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenAlreadyLike() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            LikeEntity likeEntity = likeRepository.save(new LikeEntity(userEntity, productEntity, true));

            // act
            LikeEntity result = likeService.dislike(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertEquals(likeEntity.getId(), result.getId());
                    },
                    () -> {
                        assert result != null;
                        assertFalse(result.getIsLike());
                    }
            );
        }

        @DisplayName("이미 좋아요를 하지 않은적이 있다면, 좋아요하지 않은 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenDisLike() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            LikeEntity likeEntity = likeRepository.save(new LikeEntity(userEntity, productEntity, false));

            // act
            LikeEntity result = likeService.dislike(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertEquals(likeEntity.getId(), result.getId());
                    },
                    () -> {
                        assert result != null;
                        assertFalse(result.getIsLike());
                    }
            );
        }

        @DisplayName("이미 좋아요를 한 적이 없다면, 좋아요되지 않은 객체가 반환된다.")
        @Test
        void returnLikeInfo_whenHaventLiked() {
            // arrange
            UserEntity userEntity = userRepository.save(new UserEntity("la28s5d", "password", "la28s5d@naver.com", "김소연", "소연", "2025-01-01", "FEMALE"));
            BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
            ProductEntity productEntity = productRepository.save(new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명"));

            // act
            LikeEntity result = likeService.dislike(userEntity, productEntity);

            // assert
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> {
                        assert result != null;
                        assertFalse(result.getIsLike());
                    }
            );
        }
    }
}
