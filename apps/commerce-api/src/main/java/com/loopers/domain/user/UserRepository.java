package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    UserEntity save(UserEntity user);

    Boolean existsByLoginId(String loginId);

    Optional<UserEntity> findByLoginId(String userId);

    Optional<UserEntity> findByLoginIdWithLock(String userId);

}
