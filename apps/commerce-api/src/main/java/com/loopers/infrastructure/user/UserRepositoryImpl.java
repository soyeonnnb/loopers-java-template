package com.loopers.infrastructure.user;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserEntity save(UserEntity user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Boolean existsByLoginId(String loginId) {
        return userJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public Optional<UserEntity> findByLoginId(String loginId) {
        return userJpaRepository.findByLoginId(loginId);
    }
}
