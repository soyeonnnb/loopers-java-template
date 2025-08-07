package com.loopers.infrastructure.user;

import com.loopers.domain.user.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByLoginId(String loginId);

    Optional<UserEntity> findByLoginId(String loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserEntity u WHERE u.loginId = :loginId")
    Optional<UserEntity> findByLoginIdWithLock(@Param("loginId") String loginId);
}
