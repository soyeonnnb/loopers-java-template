package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class BrandEntity extends BaseEntity {

    @Schema(name = "브랜드 명")
    @Column(nullable = false)
    private String name;

    protected BrandEntity() {

    }

    public BrandEntity(String name) {
        if (name == null || name.isBlank()) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "이름은 필수로 입력해야합니다.");
        }
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
