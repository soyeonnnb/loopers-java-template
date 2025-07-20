package com.loopers.interfaces.api.point;


import jakarta.validation.constraints.NotNull;

public class PointV1Dto {
    public record PointResponse(Long point) {
        public PointResponse(Long point) {
            this.point = point;
        }

        public static PointV1Dto.PointResponse from(Long point) {
            return new PointV1Dto.PointResponse(point);
        }
    }

    public record ChargePointRequest(@NotNull Long point) {
        public ChargePointRequest(Long point) {
            this.point = point;
        }
    }
}
