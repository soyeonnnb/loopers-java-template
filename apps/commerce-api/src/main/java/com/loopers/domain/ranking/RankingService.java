package com.loopers.domain.ranking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {

    private final RankingRepository rankingRepository;

    /**
     * 특정 날짜의 상위 랭킹 상품 ID 조회 (페이징)
     */
    public List<Long> getTopProductIds(LocalDate date, int page, int size) {
        int offset = (page - 1) * size;
        return rankingRepository.getTopProductIdList(date, offset, size);
    }

    /**
     * 특정 상품의 특정 날짜 순위 조회 (1부터 시작)
     */
    public Long getRank(LocalDate date, Long productId) {
        return rankingRepository.getProductRank(date, productId);
    }

    /**
     * 특정 상품의 특정 날짜 점수 조회
     */
    public Double getScore(LocalDate date, Long productId) {
        return rankingRepository.getScore(date, productId);
    }
}
