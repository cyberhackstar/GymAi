
// UserProfileCheckDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCheckDTO {
    private boolean exists;
    private boolean profileComplete;
    private UserProfileDTO profile;
    private String message;

    public static UserProfileCheckDTO notFound() {
        return new UserProfileCheckDTO(false, false, null, "User profile not found");
    }

    public static UserProfileCheckDTO incomplete(UserProfileDTO profile) {
        return new UserProfileCheckDTO(true, false, profile, "Profile incomplete, please fill missing details");
    }

    public static UserProfileCheckDTO complete(UserProfileDTO profile) {
        return new UserProfileCheckDTO(true, true, profile, "Profile complete");
    }
}
