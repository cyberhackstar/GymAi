package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String goal; // e.g., "weight_loss", "muscle_gain"
    private String activityLevel; // e.g., "Moderate", "High"
    private String preference; // e.g., "Vegetarian", "Non-Vegetarian"

    @Column(length = 5000) // or more
    private String dietPlan;

    @Column(length = 5000)
    private String workoutPlan;
    private Integer calories; // Total calories for the plan
    private String feedback; // User feedback on the plan
}
