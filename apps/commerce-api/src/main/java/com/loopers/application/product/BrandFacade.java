package com.loopers.application.product;

import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.BrandService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BrandFacade {
    private final BrandService brandService;

    public BrandInfo getBrandInfo(Long brandId) {
        if (brandId == null) {
            throw new CoreException(GlobalErrorType.BAD_REQUEST, "brandId 가 없습니다.");
        }
        Optional<BrandEntity> optionalBrandEntity = brandService.getBrandInfo(brandId);
        if (optionalBrandEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND, "brandId에 해당하는 브랜드 정보가 업습니다.");
        }
        return BrandInfo.from(optionalBrandEntity.get());
    }


}
