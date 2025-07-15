package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.UserErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity register(UserV1Dto.UserRegisterRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CoreException(UserErrorType.DUPLICATE_LOGIN_ID);
        }

        return userRepository.save(request.to());
    }

}
