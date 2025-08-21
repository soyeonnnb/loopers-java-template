package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "card")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardEntity extends BaseEntity {
    @Schema(name = "카드 소유자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Schema(name = "카드 번호")
    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Schema(name = "카드 타입")
    @Column(nullable = false)
    private CardType type;

    public CardEntity(UserEntity user, String number, String type) {
        if (user == null) {
            throw new CoreException(GlobalErrorType.UNAUTHORIZED, "카드 소유자는 필수입니다.");
        }
        CardValidator.validateCardNumber(number);
        this.user = user;
        this.number = number;
        this.type = CardType.from(type);
    }
}
