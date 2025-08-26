package com.gymai.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private int age;
    private double height;
    private double weight;
    private String gender;
    private String goal;
    private String activityLevel;
    private String preference;
    private String email;
}