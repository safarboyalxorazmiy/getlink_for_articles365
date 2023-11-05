package com.alcode.userFollowers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFollowerService {
    private final UserFollowerRepository userFollowerRepository;

    public Boolean create(Long fromId, Long followerId) {

        if (fromId.equals(followerId)) {
            return false;
        }

        Boolean byFromIdAndFollowerId =
                findByFromIdAndFollowerId(fromId, followerId);
        if (byFromIdAndFollowerId) {
            UserFollowersEntity entity = new UserFollowersEntity();
            entity.setFollowerId(followerId);
            entity.setFromId(fromId);
            entity.setIsSubscribed(false);
            userFollowerRepository.save(entity);

            return true;
        }

        return false;
    }

    public Boolean setSubscribed(Long fromId, Long followerId) {
        Optional<UserFollowersEntity> byFromIdAndFollowerId =
                userFollowerRepository.findByFromIdAndFollowerId(fromId, followerId);
        if (byFromIdAndFollowerId.isEmpty()) {
            return false;
        }

        UserFollowersEntity entity = byFromIdAndFollowerId.get();
        entity.setIsSubscribed(true);
        userFollowerRepository.save(entity);
        return true;
    }

    public Integer getCount(Long fromId) {
        return userFollowerRepository.countByFromIdAndIsSubscribedTrue(fromId);
    }

    private Boolean findByFromIdAndFollowerId(Long fromId, Long followerId) {
        Optional<UserFollowersEntity> byFromIdAndFollowerId =
                userFollowerRepository.findByFromIdAndFollowerId(fromId, followerId);

        return byFromIdAndFollowerId.isEmpty();
    }

    public Long getFromId(Long followerId) {
        Optional<UserFollowersEntity> byFollowerId = userFollowerRepository.findByFollowerId(followerId);
        return byFollowerId.map(UserFollowersEntity::getFromId).orElse(null);
    }
}