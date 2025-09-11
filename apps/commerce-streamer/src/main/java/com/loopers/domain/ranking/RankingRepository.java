package com.loopers.domain.ranking;

import java.time.LocalDate;

public interface RankingRepository {
    /**
     * 랭킹 점수 추가/업데이트
     */
    void incrementScore(LocalDate date, Long productId, double score);

}
