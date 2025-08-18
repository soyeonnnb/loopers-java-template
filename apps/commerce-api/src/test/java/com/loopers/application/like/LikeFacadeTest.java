package com.loopers.application.like;

import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.*;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class LikeFacadeTest {

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
    private ProductCountRepository productCountRepository;

    @Autowired
    private LikeFacade likeFacade;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("좋아요 할 때,")
    @Nested
    class Like {

        @DisplayName("동일한 상품에 대해 여러 명이 좋아요 요청을 해도 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Nested
        class ConcurrencyCondition {
            private final int SIZE = 50;
            private ProductEntity productEntity;
            private List<UserEntity> userEntityList;

            @BeforeEach
            void setup() {
                BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
                productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productEntity = productRepository.save(productEntity);


                userEntityList = new ArrayList<>();
                for (int i = 0; i < SIZE; i++) {
                    userEntityList.add(userRepository.save(new UserEntity("la28s5d" + i, "password", "la28s5d" + i + "@naver.com", "김소연", "소연", "2025-01-01", "FEMALE")));
                }
            }

            @DisplayName("(전체 유저가 좋아요하지 않음)")
            @Test
            void validLikeCount_whenAllUserDislike() throws InterruptedException {
                // arrange

                // act
                ExecutorService executor = Executors.newFixedThreadPool(SIZE);
                CountDownLatch latch = new CountDownLatch(SIZE);

                for (int i = 0; i < SIZE; i++) {
                    int finalI = i;
                    executor.submit(() -> {
                        try {
                            likeFacade.like(userEntityList.get(finalI).getLoginId(), productEntity.getId());
                        } catch (Exception e) {
                            System.out.println("실패: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                // assert
                ProductEntity repoProductEntity = productRepository.findById(productEntity.getId()).orElseThrow();
                assertEquals(SIZE, repoProductEntity.getProductCount().getLikeCount());
            }

            @DisplayName("(일부 유저가 좋아요하지 않음)")
            @Test
            void validLikeCount_whenSomeUserDislike() throws InterruptedException {
                // arrange
                for (int i = 0; i < SIZE; i += 3) {
                    likeFacade.like(userEntityList.get(i).getLoginId(), productEntity.getId());
                }

                // act
                ExecutorService executor = Executors.newFixedThreadPool(SIZE);
                CountDownLatch latch = new CountDownLatch(SIZE);

                for (int i = 0; i < SIZE; i++) {
                    int finalI = i;
                    executor.submit(() -> {
                        try {
                            likeFacade.like(userEntityList.get(finalI).getLoginId(), productEntity.getId());
                        } catch (Exception e) {
                            System.out.println("실패: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                // assert
                ProductEntity repoProductEntity = productRepository.findById(productEntity.getId()).orElseThrow();
                assertEquals(SIZE, repoProductEntity.getProductCount().getLikeCount());
            }
        }

    }

    @DisplayName("좋아요 취소할 때,")
    @Nested
    class DisLike {

        @DisplayName("동일한 상품에 대해 여러 명이 좋아요 취소 요청을 해도 상품의 좋아요 개수가 정상 반영되어야 한다.")
        @Nested
        class ConcurrencyCondition {
            private final int SIZE = 300;
            private ProductEntity productEntity;
            private List<UserEntity> userEntityList;

            @BeforeEach
            void setup() {
                BrandEntity brandEntity = brandRepository.save(new BrandEntity("브랜드"));
                productEntity = new ProductEntity(brandEntity, "상품", 1L, 1L, ProductStatus.SALE, "설명", LocalDateTime.of(2025, 1, 1, 0, 0, 0));
                ProductCountEntity productCountEntity = new ProductCountEntity(productEntity);
                ReflectionTestUtils.setField(productEntity, "productCount", productCountEntity);
                productEntity = productRepository.save(productEntity);


                userEntityList = new ArrayList<>();
                for (int i = 0; i < SIZE; i++) {
                    userEntityList.add(userRepository.save(new UserEntity("la28s5d" + i, "password", "la28s5d" + i + "@naver.com", "김소연", "소연", "2025-01-01", "FEMALE")));
                }
            }

            @DisplayName("(전체 유저가 좋아요함)")
            @Test
            void validLikeCount_whenAllUserDislike() throws InterruptedException {
                // arrange
                for (int i = 0; i < SIZE; i++) {
                    likeFacade.like(userEntityList.get(i).getLoginId(), productEntity.getId());
                }

                // act
                ExecutorService executor = Executors.newFixedThreadPool(SIZE);
                CountDownLatch latch = new CountDownLatch(SIZE);

                for (int i = 0; i < SIZE; i++) {
                    int finalI = i;
                    executor.submit(() -> {
                        try {
                            likeFacade.dislike(userEntityList.get(finalI).getLoginId(), productEntity.getId());
                        } catch (Exception e) {
                            System.out.println("실패: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                // assert
                ProductEntity repoProductEntity = productRepository.findById(productEntity.getId()).orElseThrow();
                assertEquals(0, repoProductEntity.getProductCount().getLikeCount());
            }

            @DisplayName("(일부 유저가 좋아요함)")
            @Test
            void validLikeCount_whenSomeUserDislike() throws InterruptedException {
                // arrange
                for (int i = 0; i < SIZE; i += 3) {
                    likeFacade.like(userEntityList.get(i).getLoginId(), productEntity.getId());
                }

                // act
                ExecutorService executor = Executors.newFixedThreadPool(SIZE);
                CountDownLatch latch = new CountDownLatch(SIZE);

                for (int i = 0; i < SIZE; i++) {
                    int finalI = i;
                    executor.submit(() -> {
                        try {
                            likeFacade.dislike(userEntityList.get(finalI).getLoginId(), productEntity.getId());
                        } catch (Exception e) {
                            System.out.println("실패: " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                latch.await();

                // assert
                ProductEntity repoProductEntity = productRepository.findById(productEntity.getId()).orElseThrow();
                assertEquals(0, repoProductEntity.getProductCount().getLikeCount());
            }
        }

    }

}
