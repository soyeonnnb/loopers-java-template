package com.loopers.interfaces.api.point;


public class PointV1Dto {
    public record PointResponse(Long point) {
        public static PointV1Dto.PointResponse from(Long point) {
            return new PointV1Dto.PointResponse(point);
        }

        public PointResponse(Long point) {
            this.point = point;
        }
    }
}
