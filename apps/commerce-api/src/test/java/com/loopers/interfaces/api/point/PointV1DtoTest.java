package com.loopers.interfaces.api.point;

import com.loopers.domain.user.UserEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.PointErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class PointV1DtoTest {
    @DisplayName("Point를 충전할 때,")
    @Nested
    class ChargePoint {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(longs = {
                0L, -1L, -100L
        })
        void throwsBadRequestException_whenPointLessThan0(Long point) {
            // arrange

            // act & assert
            CoreException exception = assertThrows(CoreException.class, () ->
                    new PointV1Dto.ChargePointRequest(point)
            );
            assertEquals(exception.getErrorType(), PointErrorType.POINT_MUST_BE_GREATER_THAN_0);
        }
    }
}
