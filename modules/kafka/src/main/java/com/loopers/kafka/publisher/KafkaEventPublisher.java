//package com.loopers.kafka.publisher;
//
//import com.loopers.kafka.message.KafkaEventMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CompletableFuture;
//
/// **
// * Kafka로 이벤트를 발행하는 클래스
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class KafkaEventPublisher {
//
//    private final KafkaTemplate<Object, Object> kafkaTemplate;
//
//    public <T> void publish(String topic, String key, KafkaEventMessage<T> message) {
//        log.debug("Kafka 이벤트 발행 - topic: {}, key: {}, eventType: {}", topic, key, message.getEventType());
//
//        // 비동기 전송
//        CompletableFuture<SendResult<Object, Object>> future = kafkaTemplate.send(topic, key, message);
//
//        // 콜백 처리
//        future.whenComplete((result, ex) -> {
//            if (ex == null) {
//                // 성공
//                log.debug("이벤트 발행 성공 - eventId: {}, partition: {}, offset: {}", message.getEventId(),
//                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
//            } else {
//                // 실패 (retries=3으로 자동 재시도됨)
//                log.error("이벤트 발행 실패 - eventId: {}", message.getEventId(), ex);
//            }
//        });
//    }
//}
