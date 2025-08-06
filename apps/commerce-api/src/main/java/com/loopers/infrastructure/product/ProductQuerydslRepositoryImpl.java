package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductSortOrder;
import com.loopers.domain.product.BrandEntity;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.loopers.domain.product.QProductCountEntity.productCountEntity;
import static com.loopers.domain.product.QProductEntity.productEntity;

@RequiredArgsConstructor
@Component
public class ProductQuerydslRepositoryImpl implements ProductQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductEntity> findProductsByBrandOrderBySortOrder(Optional<BrandEntity> optionalBrandEntity, ProductSortOrder order, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        optionalBrandEntity.ifPresent(brandEntity -> booleanBuilder.and(productEntity.brand.eq(brandEntity)));
        booleanBuilder.and(productEntity.status.eq(ProductStatus.SALE).or(productEntity.status.eq(ProductStatus.SOLD_OUT)));

        OrderSpecifier<?> orderSpecifier;
        if (order == null) {
            orderSpecifier = productEntity.createdAt.desc();
        } else {
            switch (order) {
                case latest -> orderSpecifier = productEntity.saleStartAt.desc();
                case likes_desc -> orderSpecifier = productEntity.productCount.likeCount.desc();
                case price_asc -> orderSpecifier = productEntity.price.asc();
                default -> orderSpecifier = productEntity.createdAt.desc();
            }
        }


        List<ProductEntity> content = queryFactory
                .select(productEntity)
                .from(productEntity)
                .join(productCountEntity)
                .where(booleanBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(productEntity.count())
                .from(productEntity)
                .where(booleanBuilder)
                .fetchOne();

        long total = count == null ? 0L : count;
        return new PageImpl<>(content, pageable, total);
    }
}
