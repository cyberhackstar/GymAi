package com.gymai.plan_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String email;

    private int age;
    private double height; // in cm
    private double weight; // in kg

    private String gender; // MALE, FEMALE
    private String goal; // WEIGHT_LOSS, WEIGHT_GAIN, MUSCLE_GAIN, MAINTENANCE

    /**
     * Maps JSON field "activity_level" → DB column "activity_level"
     * Always required (nullable = false)
     */
    @JsonProperty("activity_level")
    @Column(name = "activity_level", nullable = false)
    private String activityLevel; // SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, VERY_ACTIVE, EXTREMELY_ACTIVE

    private String preference; // VEG, NON_VEG, VEGAN
}
