package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> find(Long id);
    UserEntity save(UserEntity user);
    Boolean existsByLoginId(String loginId);

    Optional<UserEntity> findByLoginId(String userId);
}
