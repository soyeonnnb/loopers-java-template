package com.loopers.domain.user;

import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity register(UserV1Dto.UserRegisterRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CoreException(UserErrorType.DUPLICATE_LOGIN_ID);
        }

        UserEntity userEntity = userRepository.save(request.to());
        return userEntity;
    }

    @Transactional(readOnly = true)
    public UserEntity getUserInfo(String userId) {
        return userRepository.findByLoginId(userId).orElse(null);
    }

    @Transactional
    public UserEntity chargePoint(String userId, PointV1Dto.ChargePointRequest request) {
       UserEntity userEntity = userRepository.findByLoginId(userId).orElseThrow(() -> new CoreException(UserErrorType.USER_NOT_EXISTS));
       userEntity.chargePoint(request.point());
       return userEntity;
    }
}
