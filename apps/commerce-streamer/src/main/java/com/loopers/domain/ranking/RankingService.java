package com.loopers.domain.ranking;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.kafka.message.KafkaEventMessage;
import com.loopers.kafka.message.payload.OrderEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    // 가중치 설정
    private static final double VIEW_WEIGHT = 0.1;
    private static final double LIKE_WEIGHT = 0.2;
    private static final double ORDER_WEIGHT = 0.6;

    private final RankingRepository rankingRepository;
    private final ObjectMapper objectMapper;

    /**
     * 조회 점수 추가
     */
    public void addViewScore(KafkaEventMessage<?> message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            Long productId = getFieldAsLong(payload, "productId");

            if (productId == null) {
                log.warn("productId가 없습니다 - eventId: {}", message.getEventId());
                return;
            }

            double score = VIEW_WEIGHT * 1; // 0.1 * 1
            rankingRepository.incrementScore(LocalDate.now(), productId, score);

            log.debug("조회 랭킹 점수 추가 - productId: {}, score: {}", productId, score);

        } catch (Exception e) {
            log.error("조회 랭킹 점수 추가 실패", e);
        }
    }

    /**
     * 좋아요 점수 추가
     */
    public void addLikeScore(KafkaEventMessage<?> message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            Long productId = getFieldAsLong(payload, "productId");

            if (productId == null) {
                log.warn("productId가 없습니다 - eventId: {}", message.getEventId());
                return;
            }

            double score = LIKE_WEIGHT * 1; // 0.2 * 1
            rankingRepository.incrementScore(LocalDate.now(), productId, score);

            log.debug("좋아요 랭킹 점수 추가 - productId: {}, score: {}", productId, score);

        } catch (Exception e) {
            log.error("좋아요 랭킹 점수 추가 실패", e);
        }
    }

    /**
     * 좋아요 점수 차감
     */
    public void removeLikeScore(KafkaEventMessage<?> message) {
        try {
            Map<String, Object> payload = (Map<String, Object>) message.getPayload();
            Long productId = getFieldAsLong(payload, "productId");

            if (productId == null) {
                log.warn("productId가 없습니다 - eventId: {}", message.getEventId());
                return;
            }

            double score = -(LIKE_WEIGHT * 1); // -0.2 * 1
            rankingRepository.incrementScore(LocalDate.now(), productId, score);

            log.debug("좋아요 취소 랭킹 점수 차감 - productId: {}, score: {}", productId, score);

        } catch (Exception e) {
            log.error("좋아요 취소 랭킹 점수 처리 실패", e);
        }
    }

    /**
     * 주문 점수 추가
     */
    public void addOrderScore(KafkaEventMessage<?> message) {
        try {
//            Map<String, Object> payload = (Map<String, Object>) message.getPayload();

            // OrderCompletedEvent에서 orderItems 추출
            OrderEventPayload.OrderCreated payload =
                    objectMapper.convertValue(message.getPayload(), OrderEventPayload.OrderCreated.class);

            if (payload.getOrderItems() == null || payload.getOrderItems().isEmpty()) {
                log.warn("orderItems가 없습니다 - eventId: {}", message.getEventId());
                return;
            }

            for (OrderEventPayload.OrderItem item : payload.getOrderItems()) {
                Long productId = item.getProductId();
                Long price = item.getPrice();
                Long quantity = item.getQuantity();

                if (productId == null || price == null || quantity == null) {
                    log.warn("주문 아이템 정보 부족 - productId: {}, price: {}, quantity: {}",
                            productId, price, quantity);
                    continue;
                }

                // 주문 점수: 0.6 * log(price * quantity)
                double orderValue = price * quantity;
                double normalizedValue = Math.log10(orderValue + 1); // +1은 log(0) 방지
                double score = ORDER_WEIGHT * normalizedValue;

                rankingRepository.incrementScore(LocalDate.now(), productId, score);

                log.debug("주문 랭킹 점수 추가 - productId: {}, orderValue: {}, score: {}",
                        productId, orderValue, score);
            }

        } catch (Exception e) {
            log.error("주문 랭킹 점수 추가 실패", e);
        }
    }

    private Long getFieldAsLong(Map<String, Object> map, String fieldName) {
        Object value = map.get(fieldName);
        if (value == null) return null;

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("숫자 변환 실패 - field: {}, value: {}", fieldName, value);
                return null;
            }
        }

        return null;
    }
}
