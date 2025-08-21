package com.loopers.domain.payment;

import java.util.Optional;

public interface CardRepository {
    CardEntity save(CardEntity cardEntity);

    Optional<CardEntity> findById(Long cardId);
}
