// UserMapper.java
package com.gymai.plan_service.mapper;

import org.springframework.stereotype.Component;
import com.gymai.plan_service.dto.UserProfileDTO;
import com.gymai.plan_service.entity.User;

@Component
public class UserMapper {

  public UserProfileDTO toDTO(User user) {
    if (user == null)
      return null;

    UserProfileDTO dto = new UserProfileDTO();
    dto.setUserId(user.getUserId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    dto.setAge(user.getAge());
    dto.setHeight(user.getHeight());
    dto.setWeight(user.getWeight());
    dto.setGender(user.getGender());
    dto.setGoal(user.getGoal());
    dto.setActivityLevel(user.getActivityLevel());
    dto.setPreference(user.getPreference());
    dto.setProfileComplete(isProfileComplete(user));

    return dto;
  }

  public User toEntity(UserProfileDTO dto) {
    if (dto == null)
      return null;

    User user = new User();
    user.setUserId(dto.getUserId());
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    user.setAge(dto.getAge());
    user.setHeight(dto.getHeight());
    user.setWeight(dto.getWeight());
    user.setGender(dto.getGender());
    user.setGoal(dto.getGoal());
    user.setActivityLevel(dto.getActivityLevel());
    user.setPreference(dto.getPreference());

    return user;
  }

  private boolean isProfileComplete(User user) {
    return user.getName() != null && !user.getName().trim().isEmpty() &&
        user.getEmail() != null && !user.getEmail().trim().isEmpty() &&
        user.getAge() != null && user.getAge() > 0 &&
        user.getHeight() != null && user.getHeight() > 0 &&
        user.getWeight() != null && user.getWeight() > 0 &&
        user.getGender() != null && !user.getGender().trim().isEmpty() &&
        user.getGoal() != null && !user.getGoal().trim().isEmpty() &&
        user.getActivityLevel() != null && !user.getActivityLevel().trim().isEmpty() &&
        user.getPreference() != null && !user.getPreference().trim().isEmpty();
  }
}