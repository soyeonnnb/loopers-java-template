package com.loopers.application.user;

import com.loopers.application.example.ExampleInfo;
import com.loopers.domain.example.ExampleModel;
import com.loopers.domain.example.ExampleService;
import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.user.UserV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo register(UserV1Dto.UserRegisterRequest request) {
        UserEntity userEntity = userService.register(request);
        return UserInfo.from(userEntity);
    }

}
