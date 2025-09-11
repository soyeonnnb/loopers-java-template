package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRankingRepository implements RankingRepository {

    private static final String KEY_PREFIX = "loopers:rank:product:all:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long TTL_DAYS = 2;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Long getProductRank(LocalDate date, Long productId) {
        String key = generateKey(date);
        String productIdString = productId.toString();

        try {
            Long rank = stringRedisTemplate.opsForZSet().reverseRank(key, productIdString);
            return rank != null ? rank + 1 : null; // 1부터 시작, 없으면 null

        } catch (Exception e) {
            log.error("상품 순위 조회 실패 - date: {}, productId: {}", date, productId, e);
            return null;
        }
    }

    @Override
    public List<Long> getTopProductIdList(LocalDate date, int offset, int limit) {
        String key = generateKey(date);

        try {
            Set<String> topProductList = stringRedisTemplate.opsForZSet()
                    .reverseRange(key, offset, offset + limit - 1);

            List<Long> productIdList = new ArrayList<>();
            log.info("=====디버깅===");
            log.info("생성된 키: {}", key);
            log.info("조회 파라미터 - date: {}, offset: {}, limit: {}", date, offset, limit);

            if (topProductList != null) {
                for (String productId : topProductList) {
                    try {
                        productIdList.add(Long.parseLong(productId));
                    } catch (NumberFormatException e) {
                        log.warn("잘못된 상품 ID 형식: {}", productId);
                    }
                }
            }

            return productIdList;

        } catch (Exception e) {
            log.error("상위 상품 ID 조회 실패 - date: {}, offset: {}, limit: {}", date, offset, limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public Double getScore(LocalDate date, Long productId) {
        String key = generateKey(date);
        String productIdString = String.valueOf(productId);

        Double score = stringRedisTemplate.opsForZSet().score(key, productIdString);
        return score != null ? score : 0.0;
    }

    private String generateKey(LocalDate date) {
        return KEY_PREFIX + date.format(DATE_FORMATTER);
    }
}

