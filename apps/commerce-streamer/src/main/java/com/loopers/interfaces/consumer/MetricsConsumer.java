package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.event.EventHandled;
import com.loopers.domain.event.EventHandledRepository;
import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.domain.metrics.ProductMetricsRepository;
import com.loopers.kafka.EventTypes;
import com.loopers.kafka.KafkaTopics;
import com.loopers.kafka.message.KafkaEventMessage;
import com.loopers.kafka.message.payload.CatalogEventPayload;
import com.loopers.kafka.message.payload.OrderEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 메트릭 집계 Consumer
 * 일별 상품 통계를 product_metrics 테이블에 UPSERT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsConsumer {

    private static final String CONSUMER_NAME = "METRICS";

    private final ProductMetricsRepository productMetricsRepository;
    private final EventHandledRepository eventHandledRepository;
    private final ObjectMapper objectMapper;
    private final com.loopers.support.DlqPublisher dlqPublisher;


    @KafkaListener(
            topics = {KafkaTopics.CATALOG_EVENTS, KafkaTopics.ORDER_EVENTS},
            groupId = "metrics-batch-group",
            containerFactory = "BATCH_LISTENER_DEFAULT"
    )
    @Transactional
    public void consumeBatch(
            List<KafkaEventMessage<?>> messages,  // String -> KafkaEventMessage로 변경
            @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
            Acknowledgment ack
    ) {
        log.info("배치 처리 시작 - {} 건", messages.size());

        int processedCount = 0;
        int failedCount = 0;

        for (int i = 0; i < messages.size(); i++) {
            KafkaEventMessage<?> message = messages.get(i);  // 직접 사용
            String topic = topics.get(i);

            if (message == null) {
                log.warn("빈 메시지 스킵");
                continue;
            }

            try {
                String eventId = message.getEventId();

                // JSON 파싱 단계 제거 (이미 역직렬화됨)

                // 1. 멱등성 체크
                if (eventHandledRepository.existsByEventIdAndConsumerName(eventId, CONSUMER_NAME)) {
                    log.debug("이미 처리된 이벤트 스킵 - eventId: {}", eventId);
                    continue;
                }

                // 2. Version 체크
                Long eventVersion = message.getVersion() != null
                        ? message.getVersion().longValue()
                        : System.currentTimeMillis() / 1000;

                Optional<EventHandled> latestProcessed = eventHandledRepository
                        .findLatestVersion(message.getAggregateId(), CONSUMER_NAME);

                if (latestProcessed.isPresent() &&
                        latestProcessed.get().getEventVersion() >= eventVersion) {
                    log.debug("구 버전 이벤트 스킵 - eventId: {}", eventId);
                    continue;
                }


                log.info("이벤트 타입:   {}", message.getEventType());

                // 3. 이벤트 처리
                switch (message.getEventType()) {
                    case "LikeEvent" -> handleLikeAdded(message);
                    case "DisLikeEvent" -> handleLikeRemoved(message);
                    case "OrderCreatedEvent" -> handleOrderCreated(message);
                    case "OrderCompletedEvent" -> handleOrderConfirmed(message);
                    case EventTypes.ORDER_CANCELLED -> handleOrderCancelled(message);
                    case "PaymentSuccessEvent" -> handlePaymentCompleted(message);
                    case "PaymentFailEvent" -> handlePaymentFailed(message);
                    default -> log.debug("메트릭 처리 대상 아님 - type: {}", message.getEventType());
                }

                // 4. 처리 완료 기록
                eventHandledRepository.save(
                        EventHandled.create(
                                eventId,
                                CONSUMER_NAME,
                                message.getEventType(),
                                message.getAggregateId(),
                                eventVersion
                        )
                );

                processedCount++;

            } catch (Exception e) {
                log.error("개별 메시지 처리 실패", e);
                failedCount++;

                // DLQ로 전송 - JSON 직렬화 필요
                try {
                    String messageJson = objectMapper.writeValueAsString(message);
                    dlqPublisher.sendToDlq(topic, messageJson, CONSUMER_NAME, e.getMessage());
                } catch (Exception jsonError) {
                    log.error("DLQ 전송 중 JSON 변환 실패", jsonError);
                }
            }
        }

        // 5. 배치 전체 ACK
        ack.acknowledge();
        log.info("배치 처리 완료 - 처리: {}/{} 건", processedCount, messages.size());
    }

    /**
     * 좋아요 추가 처리
     */
    private void handleLikeAdded(KafkaEventMessage<?> message) {
        CatalogEventPayload.LikeAdded payload =
                objectMapper.convertValue(message.getPayload(), CatalogEventPayload.LikeAdded.class);

        Long productId = payload.getProductId();
        LocalDate today = LocalDate.now();

        // 오늘 날짜의 메트릭 조회 or 생성
        ProductMetrics metrics = productMetricsRepository
                .findByProductIdAndMetricDate(productId, today)
                .orElse(ProductMetrics.builder()
                        .productId(productId)
                        .metricDate(today)
                        .likeCount(0L)
                        .orderCount(0L)
                        .salesQuantity(0L)
                        .updatedAt(LocalDateTime.now())
                        .build());

        // 좋아요 수 증가
        metrics.addLike();

        productMetricsRepository.save(metrics);
        log.info("좋아요 메트릭 업데이트 - productId: {}, likeCount: {}",
                productId, metrics.getLikeCount());
    }

    /**
     * 좋아요 제거 처리
     */
    private void handleLikeRemoved(KafkaEventMessage<?> message) {
        CatalogEventPayload.LikeRemoved payload =
                objectMapper.convertValue(message.getPayload(), CatalogEventPayload.LikeRemoved.class);

        Long productId = payload.getProductId();
        LocalDate today = LocalDate.now();

        ProductMetrics metrics = productMetricsRepository
                .findByProductIdAndMetricDate(productId, today)
                .orElse(ProductMetrics.builder()
                        .productId(productId)
                        .metricDate(today)
                        .likeCount(0L)
                        .orderCount(0L)
                        .salesQuantity(0L)
                        .updatedAt(LocalDateTime.now())
                        .build());

        metrics.removeLike();

        productMetricsRepository.save(metrics);
        log.info("좋아요 메트릭 감소 - productId: {}, likeCount: {}",
                productId, metrics.getLikeCount());
    }

    /**
     * 주문 생성 처리
     */
    private void handleOrderCreated(KafkaEventMessage<?> message) {
        log.info("주문 메트릭 처리 - aggregateId: {}", message.getAggregateId());
    }

    /**
     * 주문 확정 처리
     */
    private void handleOrderConfirmed(KafkaEventMessage<?> message) {

        OrderEventPayload.OrderCreated payload =
                objectMapper.convertValue(message.getPayload(), OrderEventPayload.OrderCreated.class);

        LocalDate today = LocalDate.now();
        log.info("주문 생성 처리");

        // 주문의 각 상품별로 처리
        for (OrderEventPayload.OrderItem item : payload.getOrderItems()) {
            Long productId = item.getProductId();

            ProductMetrics metrics = productMetricsRepository
                    .findByProductIdAndMetricDate(productId, today)
                    .orElse(ProductMetrics.builder()
                            .productId(productId)
                            .metricDate(today)
                            .likeCount(0L)
                            .orderCount(0L)
                            .salesQuantity(0L)
                            .updatedAt(LocalDateTime.now())
                            .build());

            // 주문 수와 판매량 증가
            metrics.addOrder(Long.valueOf(item.getQuantity()));

            productMetricsRepository.save(metrics);
            log.info("주문 메트릭 업데이트 - productId: {}, orderCount: {}, salesQty: {}",
                    productId, metrics.getOrderCount(), metrics.getSalesQuantity());
        }
        log.info("주문 확정 이벤트 처리 - aggregateId: {}", message.getAggregateId());
    }

    /**
     * 주문 취소 처리
     */
    private void handleOrderCancelled(KafkaEventMessage<?> message) {
        log.info("주문 취소 이벤트 처리 - aggregateId: {}", message.getAggregateId());
    }

    /**
     * 결제 완료 처리
     */
    private void handlePaymentCompleted(KafkaEventMessage<?> message) {
        log.info("결제 완료 이벤트 처리 - aggregateId: {}", message.getAggregateId());
    }

    /**
     * 결제 실패 처리
     */
    private void handlePaymentFailed(KafkaEventMessage<?> message) {
        log.info("결제 실패 이벤트 처리 - aggregateId: {}, payload: {}",
                message.getAggregateId(), message.getPayload());
    }
}
