package com.loopers.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@Entity
@Table(name = "product_count")
@Getter
public class ProductCountEntity extends BaseEntity implements Serializable {
    @JoinColumn
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ProductEntity product;

    @Schema(name = "총 좋아요 수")
    @Column(nullable = false)
    private Long likeCount;

    protected ProductCountEntity() {

    }

    public ProductCountEntity(ProductEntity productEntity) {
        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품이 존재하지 않습니다.");
        }
        this.product = productEntity;
        this.likeCount = 0L;
    }

    public ProductCountEntity(ProductEntity productEntity, Long likeCount) {
        if (productEntity == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "상품이 존재하지 않습니다.");
        }
        if (likeCount == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "좋아요 수는 null이 될 수 없습니다.");
        } else if (likeCount < 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "좋아요 수는 음수가 될 수 없습니다.");
        }
        this.product = productEntity;
        this.likeCount = likeCount;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
        if (this.likeCount < 0) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "좋아요 수는 음수가 될 수 없습니다.");
        }
    }
}
