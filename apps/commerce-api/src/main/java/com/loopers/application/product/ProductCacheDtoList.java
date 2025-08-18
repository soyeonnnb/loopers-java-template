package com.loopers.application.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCacheDtoList implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<ProductCacheDto> list;

    public ProductCacheDtoList(List<ProductCacheDto> list) {
        this.list = list;
    }
}
