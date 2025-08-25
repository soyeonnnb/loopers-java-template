package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.CardEntity;
import com.loopers.domain.payment.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CardRepositoryImpl implements CardRepository {
    private final CardJpaRepository cardJpaRepository;

    @Override
    public CardEntity save(CardEntity cardEntity) {
        return cardJpaRepository.save(cardEntity);
    }

    @Override
    public Optional<CardEntity> findById(Long cardId) {
        return cardJpaRepository.findById(cardId);
    }
}
