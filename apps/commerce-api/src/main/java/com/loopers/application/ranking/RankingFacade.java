package com.loopers.application.ranking;

import com.loopers.application.product.ProductInfo;
import com.loopers.domain.like.LikeEntity;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductEntity;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RankingFacade {

    private final RankingService rankingService;
    private final ProductService productService;
    private final UserService userService;
    private final LikeService likeService;

    @Transactional(readOnly = true)
    public List<ProductInfo> getRankingList(String userId, LocalDate date, Integer page, Integer size) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        List<Long> productIdList = rankingService.getTopProductIds(date, page, size);
        List<ProductEntity> productEntityList = productService.getProductList(productIdList);
        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);

        if (optionalUserEntity.isEmpty()) {
            List<ProductInfo> productInfoList = new ArrayList<>();
            for (ProductEntity product : productEntityList) {
                Long rank = rankingService.getRank(date, product.getId());
                Double score = rankingService.getScore(date, product.getId());
                productInfoList.add(ProductInfo.from(product, false, rank, score));
            }
            return productInfoList;
        } else {
            List<ProductInfo> productInfoList = new ArrayList<>();
            for (ProductEntity product : productEntityList) {
                boolean isLike = false;
                Optional<LikeEntity> optionalLikeEntity = likeService.getUserLikeProduct(optionalUserEntity.get().getId(), product.getId());
                isLike = optionalLikeEntity.isPresent() && optionalLikeEntity.get().getIsLike();
                Long rank = rankingService.getRank(date, product.getId());
                Double score = rankingService.getScore(date, product.getId());
                productInfoList.add(ProductInfo.from(product, isLike, rank, score));
            }
            return productInfoList;
        }
    }
}
