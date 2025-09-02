// UserProfileDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String name;
    private String email;
    private Integer age;
    private Double height;
    private Double weight;
    private String gender;
    private String goal;
    private String activityLevel;
    private String preference;
    private boolean profileComplete;

    // Helper method to check if profile is complete
    public boolean isProfileComplete() {
        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                age != null && age > 0 &&
                height != null && height > 0 &&
                weight != null && weight > 0 &&
                gender != null && !gender.trim().isEmpty() &&
                goal != null && !goal.trim().isEmpty() &&
                activityLevel != null && !activityLevel.trim().isEmpty() &&
                preference != null && !preference.trim().isEmpty();
    }

    // Set profileComplete based on validation
    public void updateProfileComplete() {
        this.profileComplete = isProfileComplete();
    }
}