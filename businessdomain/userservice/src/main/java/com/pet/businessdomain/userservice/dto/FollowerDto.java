package com.pet.businessdomain.userservice.dto;

public class FollowerDto {
    private Long id;
    private Long followerId;
    private Long followedId;
    // otros campos que devuelva FollowerDto
    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowedId() {
        return followedId;
    }
}
