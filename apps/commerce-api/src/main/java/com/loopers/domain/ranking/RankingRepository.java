package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface RankingRepository {
    /**
     * 특정 상품의 순위 조회 (1부터 시작, 없으면 null)
     */
    Long getProductRank(LocalDate date, Long productId);

    /**
     * 상위 N개 상품 ID 조회 (페이징)
     */
    List<Long> getTopProductIdList(LocalDate date, int offset, int limit);

    /**
     * 특정 상품의 특정 날짜 점수 조회
     */
    Double getScore(LocalDate date, Long productId);
}
