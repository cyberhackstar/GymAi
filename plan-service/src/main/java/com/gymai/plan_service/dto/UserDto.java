package com.gymai.plan_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private int age;

    @JsonProperty("height")
    private double height;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("goal")
    private String goal;

    @JsonProperty("activityLevel")
    private String activityLevel;

    @JsonProperty("preference")
    private String preference;

    @JsonProperty("email")
    private String email;
}
