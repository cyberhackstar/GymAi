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
    private UserProfileDTO user;
    private String message;

    public static UserProfileCheckDTO notFound() {
        return new UserProfileCheckDTO(false, false, null, "User not found");
    }

    public static UserProfileCheckDTO incomplete(UserProfileDTO user) {
        return new UserProfileCheckDTO(true, false, user, "Profile incomplete");
    }

    public static UserProfileCheckDTO complete(UserProfileDTO user) {
        return new UserProfileCheckDTO(true, true, user, "Profile complete");
    }
}
