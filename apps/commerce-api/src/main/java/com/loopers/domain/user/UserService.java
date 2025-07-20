package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity register(UserV1Dto.UserRegisterRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CoreException(UserErrorType.DUPLICATE_LOGIN_ID);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        UserEntity userEntity = request.to(encodedPassword);
        return userRepository.save(userEntity);
    }

    @Transactional(readOnly = true)
    public UserEntity getUserInfo(String userId) {
        return userRepository.findByLoginId(userId).orElse(null);
    }

    @Transactional
    public UserEntity chargePoint(String userId, Long point) {
        UserEntity userEntity = userRepository.findByLoginId(userId).orElseThrow(() -> new CoreException(UserErrorType.USER_NOT_EXISTS));
        userEntity.chargePoint(point);
        return userEntity;
    }
}
