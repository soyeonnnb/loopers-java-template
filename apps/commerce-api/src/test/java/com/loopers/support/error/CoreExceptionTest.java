package com.loopers.support.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoreExceptionTest {
    @DisplayName("BaseErrorType 기반의 예외 생성 시, 메시지를 잘 가져온다")
    @Test
    void messageShouldBeErrorTypeMessage() {
        BaseErrorType[] allErrorTypes = concatArrays(BaseErrorType.class,
                GlobalErrorType.values(),
                UserErrorType.values()
        );

        for (BaseErrorType errorType : allErrorTypes) {
            CoreException exception = new CoreException(errorType);
            assertThat(exception.getMessage()).isEqualTo(errorType.getMessage());
        }
    }

    @SafeVarargs
    private static <T> T[] concatArrays(Class<T> clazz, T[]... arrays) {
        return java.util.Arrays.stream(arrays)
                .flatMap(java.util.Arrays::stream)
                .toArray(size -> (T[]) java.lang.reflect.Array.newInstance(clazz, size));
    }

    @DisplayName("ErrorType 기반의 예외 생성 시, 별도의 메시지가 주어지면 해당 메시지를 사용한다.")
    @Test
    void messageShouldBeCustomMessage_whenCustomMessageIsNotNull() {
        // arrange
        String customMessage = "custom message";

        // act
        CoreException exception = new CoreException(GlobalErrorType.INTERNAL_ERROR, customMessage);

        // assert
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}
