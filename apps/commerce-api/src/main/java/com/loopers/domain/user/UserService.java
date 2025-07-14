package com.loopers.domain.user;

import com.loopers.interfaces.api.user.UserV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity register(UserV1Dto.UserRegisterRequest request) {

        UserEntity user = userRepository.save(request.to());

        return user;
    }

}
