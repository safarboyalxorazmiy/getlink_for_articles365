package com.alcode.userFollowers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFollowerRepository extends JpaRepository<UserFollowersEntity, Long> {
    Optional<UserFollowersEntity> findByFromIdAndFollowerId(Long fromId, Long followerId);

    Integer countByFromIdAndIsSubscribedTrue(Long fromId);

    Optional<UserFollowersEntity> findByFollowerId(Long followerId);
}