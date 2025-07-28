package com.loopers.application.user;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.GlobalErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo register(UserV1Dto.UserRegisterRequest request) {
        UserEntity userEntity = userService.register(request);
        return UserInfo.from(userEntity);
    }

    public UserInfo getUserInfo(String userId) {
        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);
        if (optionalUserEntity.isEmpty()) {
            throw new CoreException(GlobalErrorType.NOT_FOUND);
        }
        return UserInfo.from(optionalUserEntity.get());
    }

    public Long getUserPoint(String userId) {
        Optional<UserEntity> optionalUserEntity = userService.getUserInfo(userId);
        return optionalUserEntity.map(UserEntity::getPoint).orElse(null);
    }

    public Long chargeUserPoint(String userId, PointV1Dto.ChargePointRequest request) {
        UserEntity userEntity = userService.chargePoint(userId, request.point());
        return userEntity.getPoint();
    }
}
