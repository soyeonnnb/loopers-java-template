package com.loopers.domain.like;

import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.UserEntity;
import com.loopers.interfaces.listener.like.DisLikeEvent;
import com.loopers.interfaces.listener.like.LikeEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LikeEntity like(UserEntity userEntity, ProductEntity productEntity) {
        if (userEntity == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다.");
        }
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserAndProduct(userEntity, productEntity);
        LikeEntity likeEntity = optionalLikeEntity.orElse(new LikeEntity(userEntity, productEntity, false));

        if (!likeEntity.getIsLike()) {
            eventPublisher.publishEvent(new LikeEvent(productEntity.getId(), userEntity.getId()));
        }
        likeEntity.like();
        return likeRepository.save(likeEntity);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = "product", key = "'product:' + #productId")
    public void increaseProductLikeCount(Long productId) {
        ProductEntity productEntity = productRepository.getProductInfoWithLock(productId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다."));
        productEntity.increaseLikeCount();
    }

    @Transactional
    public LikeEntity dislike(UserEntity userEntity, ProductEntity productEntity) {
        if (userEntity == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다.");
        }
        Optional<LikeEntity> optionalLikeEntity = likeRepository.findByUserAndProduct(userEntity, productEntity);
        if (optionalLikeEntity.isEmpty()) { // 아직 좋아요한 적이 없음
            return null;
        } else {
            LikeEntity likeEntity = optionalLikeEntity.get();
            if (likeEntity.getIsLike()) {
                eventPublisher.publishEvent(new DisLikeEvent(productEntity.getId(), userEntity.getId()));
            }
            likeEntity.dislike();
            return likeRepository.save(likeEntity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = "product", key = "'product:' + #productId")
    public void decreaseProductLikeCount(Long productId) {
        ProductEntity productEntity = productRepository.getProductInfoWithLock(productId).orElseThrow(() -> new CoreException(GlobalErrorType.NOT_FOUND, "상품 정보가 없습니다."));
        productEntity.decreaseLikeCount();
    }


    @Transactional(readOnly = true)
    public List<LikeEntity> getUserLikeList(UserEntity userEntity) {
        if (userEntity == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "사용자 정보가 없습니다.");
        }
        return likeRepository.findAllByUserAndIsLikeIsTrue(userEntity);
    }

    @Transactional(readOnly = true)
    public Optional<LikeEntity> getUserLikeProduct(Long userId, Long productId) {
        if (userId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "사용자 정보가 없습니다.");
        }

        if (productId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품 정보가 없습니다.");
        }

        return likeRepository.findByUserIdAndProductId(userId, productId);
    }
}
