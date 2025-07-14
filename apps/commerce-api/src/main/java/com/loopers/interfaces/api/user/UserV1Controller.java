package com.loopers.interfaces.api.user;

import com.loopers.application.example.ExampleFacade;
import com.loopers.application.example.ExampleInfo;
import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.example.ExampleV1ApiSpec;
import com.loopers.interfaces.api.example.ExampleV1Dto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}
