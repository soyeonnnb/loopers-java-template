package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class BrandEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    public String getName() {
        return this.name;
    }
}
