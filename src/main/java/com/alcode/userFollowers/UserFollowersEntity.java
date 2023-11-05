package com.alcode.userFollowers;

import com.alcode.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_followers")
public class UserFollowersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_id")
    private Long fromId;

    @Column(name = "follower_id")
    private Long followerId;

    private Boolean isSubscribed;
}