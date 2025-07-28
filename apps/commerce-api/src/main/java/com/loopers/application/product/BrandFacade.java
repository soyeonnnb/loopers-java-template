package com.loopers.application.product;

import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import com.loopers.support.error.ProductErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BrandFacade {
    private final BrandService brandService;

    public BrandInfo getBrandInfo(Long brandId) {
        if (brandId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST);
        }
        Optional<BrandEntity> optionalBrandEntity = brandService.getBrandInfo(brandId);
        if (optionalBrandEntity.isEmpty()) {
            throw new CoreException(ProductErrorType.BRAND_ID_CANNOT_BE_NULL);
        }
        return BrandInfo.from(optionalBrandEntity.get());
    }


}
