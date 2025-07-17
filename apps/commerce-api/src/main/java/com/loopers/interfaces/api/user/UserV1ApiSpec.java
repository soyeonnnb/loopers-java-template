package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User V1 API", description = "User API")
public interface UserV1ApiSpec {

    @Operation(
            summary = "회원가입",
            description = "회원가입을 합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> register(
            @Schema(name = "회원가입 정보", description = "회원가입 정보")
            @RequestBody UserV1Dto.UserRegisterRequest userRegisterRequest
    );

    @Operation(
            summary = "내 정보 조회",
            description = "내 정보를 조회합니다."
    )
    ApiResponse<UserV1Dto.UserResponse> getUserInfo(
            @RequestHeader("X-USER-ID") String userId
    );

}
