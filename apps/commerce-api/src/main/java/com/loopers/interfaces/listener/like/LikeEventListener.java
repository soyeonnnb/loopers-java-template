package com.loopers.interfaces.listener.like;

import com.loopers.domain.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventListener {

    private final ApplicationEventPublisher eventPublisher;
    private final LikeService likeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLike(LikeEvent event) {
        log.info("좋아요 이벤트 처리: productId={}", event.getAggregateId());
        likeService.increaseProductLikeCount(event.getProductId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDisLike(DisLikeEvent event) {
        log.info("좋아요 취소 이벤트 처리: productId={}", event.getAggregateId());
        likeService.decreaseProductLikeCount(event.getProductId());
    }
}
