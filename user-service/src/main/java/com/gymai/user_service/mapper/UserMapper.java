package com.gymai.user_service.mapper;

import com.gymai.user_service.dto.UserDto;
import com.gymai.user_service.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(UserProfile profile) {
        return new UserDto(
                profile.getUserId(),
                profile.getName(),
                profile.getAge(),
                profile.getHeight(),
                profile.getWeight(),
                profile.getGender(),
                profile.getGoal(),
                profile.getActivityLevel(),
                profile.getPreference(),
                profile.getEmail());
    }

    public UserProfile toEntity(UserDto dto) {
        return new UserProfile(
                dto.getUserId(),
                dto.getName(),
                dto.getEmail(),
                dto.getAge(),
                dto.getHeight(),
                dto.getWeight(),
                dto.getGender(),
                dto.getGoal(),
                dto.getActivityLevel(),
                dto.getPreference());
    }
}