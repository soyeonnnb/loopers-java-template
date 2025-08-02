package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@Table(name = "likes")
public class LikeEntity extends BaseEntity {

    @Schema(name = "좋아요 유저")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Schema(name = "좋아요 상품")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductEntity product;

    @Schema(name = "좋아요 여부")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeEntity that = (LikeEntity) o;
        return Objects.equals(user, that.user) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
