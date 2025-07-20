package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @Override
    @PostMapping
    public ApiResponse<UserV1Dto.UserResponse> register(@RequestBody @Valid UserV1Dto.UserRegisterRequest userRegisterRequest) {
        UserInfo userInfo = userFacade.register(userRegisterRequest);
        return ApiResponse.success(UserV1Dto.UserResponse.from(userInfo));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserV1Dto.UserResponse> getUserInfo(@RequestHeader("X-USER-ID") String userId) {
        UserInfo userInfo = userFacade.getUserInfo(userId);
        return ApiResponse.success(UserV1Dto.UserResponse.from(userInfo));
    }
}
