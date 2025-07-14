package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.example.ExampleV1Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User V1 API", description = "User API")
public interface UserV1ApiSpec {

    @Operation(
            summary = "회원가입",
            description = "회원가입을 합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> register(
            @Schema(name = "예시 ID", description = "조회할 예시의 ID")
            @RequestBody UserV1Dto.UserRegisterRequest userRegisterRequest
    );
}
