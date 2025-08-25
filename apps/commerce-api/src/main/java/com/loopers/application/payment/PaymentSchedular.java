package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSchedular {

    private final PaymentService paymentService;
    private final PaymentFacade paymentFacade;

    @Scheduled(cron = "0 */1 * * * ?") // 매 1분마다
    public void getPaymentInfos() {
        LocalDateTime now = LocalDateTime.now().withSecond(0);
        LocalDateTime before2Minutes = now.minusMinutes(2L);
        LocalDateTime before1Minutes = now.minusMinutes(1L);
        log.info("{}시 {}분 ~ {}시 {}분 결제 정보 업데이트 안된 주문 패치 작업 시작", before2Minutes.getHour(), before2Minutes.getMinute(), before1Minutes.getHour(), before1Minutes.getMinute());
        paymentFacade.paymentFetch(before2Minutes, before1Minutes);
        log.info("{}시 {}분 ~ {}시 {}분 결제 정보 업데이트 안된 주문 패치 작업 종료", before2Minutes.getHour(), before2Minutes.getMinute(), before1Minutes.getHour(), before1Minutes.getMinute());
    }

}
