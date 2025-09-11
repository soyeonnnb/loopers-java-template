package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRankingRepository implements RankingRepository {

    private static final String KEY_PREFIX = "loopers:rank:product:all:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long TTL_DAYS = 2;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void incrementScore(LocalDate date, Long productId, double score) {
        String key = generateKey(date);
        String productIdString = productId.toString();

        try {
            // ZINCRBY로 점수 증가
            stringRedisTemplate.opsForZSet().incrementScore(key, productIdString, score);

            // TTL 설정 (키가 새로 생성되었을 때만)
            if (stringRedisTemplate.getExpire(key) == -1) {
                stringRedisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);
                log.debug("랭킹 키 TTL 설정 - key: {}", key);
            }

        } catch (Exception e) {
            log.error("랭킹 점수 업데이트 실패 - date: {}, productId: {}, score: {}",
                    date, productId, score, e);
            throw new RuntimeException("랭킹 점수 업데이트 실패", e);
        }
    }

    private String generateKey(LocalDate date) {
        return KEY_PREFIX + date.format(DATE_FORMATTER);
    }
}
