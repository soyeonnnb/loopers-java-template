package com.loopers.interfaces.api.point;


import com.loopers.support.error.CoreException;
import com.loopers.support.error.PointErrorType;

public class PointV1Dto {
    public record PointResponse(Long point) {
        public static PointV1Dto.PointResponse from(Long point) {
            return new PointV1Dto.PointResponse(point);
        }

        public PointResponse(Long point) {
            this.point = point;
        }
    }

    public record ChargePointRequest(Long point) {
        public ChargePointRequest(Long point) {
            if (point <= 0) {
                throw new CoreException(PointErrorType.POINT_MUST_BE_GREATER_THAN_0);
            }
            this.point = point;
        }
    }
}
