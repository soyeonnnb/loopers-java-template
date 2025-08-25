//package com.loopers.configuration;
//
//import com.loopers.support.error.CoreException;
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
//import io.github.resilience4j.timelimiter.TimeLimiter;
//import io.github.resilience4j.timelimiter.TimeLimiterConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.ResourceAccessException;
//
//import java.net.ConnectException;
//import java.net.SocketTimeoutException;
//import java.time.Duration;
//
//@Configuration
//public class ResilienceConfig {
//
//    @Bean
//    public CircuitBreaker pgPaymentCircuitBreaker() {
//        return CircuitBreaker.of("pgPayment", CircuitBreakerConfig.custom()
//                .failureRateThreshold(70) // 기본 성공률 60 + 여유분
//                .slowCallRateThreshold(90) // slow call 50% 이상
//                .slowCallDurationThreshold(Duration.ofSeconds(6L)) // 최대 처리시간 5초 + 여유분
//                .minimumNumberOfCalls(3) // 보수적으로
//                .waitDurationInOpenState(Duration.ofSeconds(10L))
//                .permittedNumberOfCallsInHalfOpenState(2)
//                .recordExceptions(
//                        ConnectException.class,
//                        SocketTimeoutException.class,
//                        ResourceAccessException.class,
//                        HttpServerErrorException.class // 5xx
//                )
//                .ignoreExceptions(
//                        HttpClientErrorException.BadRequest.class, // 4xx
//                        CoreException.class
//                )
//                .build());
//    }
//
//    @Bean
//    public TimeLimiter pgTimeLimiter() {
//        return TimeLimiter.of("pgTimeLimiter", TimeLimiterConfig.custom()
//                // 총 타임아웃: 8초 (처리 5초 + 네트워크 3초)
//                .timeoutDuration(Duration.ofSeconds(8))
//                .build());
//    }
//}
