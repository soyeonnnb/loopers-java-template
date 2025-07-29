package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "likes")
public class LikeEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

    @Column(nullable = false)
    private Boolean isLike;

    protected LikeEntity() {

    }

    public LikeEntity(UserEntity userEntity, ProductEntity productEntity, Boolean isLike) {
        if (userEntity == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "좋아요 생성 시 사용자는 필수입니다.");
        }

        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "좋아요 생성 시 상품은 필수입니다.");
        }

        this.user = userEntity;
        this.product = productEntity;
        this.isLike = isLike;
    }

    public void like() {
        this.isLike = true;
    }

    public void dislike() {
        this.isLike = false;
    }
}
