package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandEntity that = (BrandEntity) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
